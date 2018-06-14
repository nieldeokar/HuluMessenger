package com.nieldeokar.hurumessenger

import android.app.Application
import android.provider.Settings
import android.util.Log
import com.nieldeokar.hurumessenger.database.dao.AccountDao
import com.nieldeokar.hurumessenger.database.entity.AccountEntity
import com.nieldeokar.hurumessenger.generator.PacketGenerator
import com.nieldeokar.hurumessenger.models.Account
import com.nieldeokar.hurumessenger.packets.LocalAddressCard
import com.nieldeokar.hurumessenger.services.LocalTransport
import com.nieldeokar.hurumessenger.utils.NetworkUtils
import com.nieldeokar.hurumessenger.utils.Utils
import com.nileshdeokar.healthapp.database.AppDatabase
import timber.log.Timber

class HuruApp : Application() {

    companion object {

        lateinit var appInstance : HuruApp
        lateinit var account : Account
        lateinit var localTransport: LocalTransport

    }

    override fun onCreate() {
        super.onCreate()
        appInstance = this
        localTransport = LocalTransport()
        localTransport.initialise()

        account = Account()
        account.devicId = (application as HuruApp).getDeviceID()
        account.name = ""
        val addressCard = LocalAddressCard()
        addressCard.localV4Address = NetworkUtils.getLocalIpV4Address()!!
        addressCard.localV4Port = LocalTransport.listeningPort
        account.localAddressCard = addressCard.toBytes()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            Timber.plant(CrashReportingTree())
        }
    }

    private class CrashReportingTree : Timber.Tree() {
        override fun log(priority: Int, tag: String, message: String, t: Throwable?) {
            if (priority == Log.VERBOSE || priority == Log.DEBUG) {
                return
            }
        }
    }

    fun getAccount() : Account?{
        return account
    }

    fun getDatabase(): AppDatabase? {
        return AppDatabase.getInstance(this)
    }

    fun getDeviceID() : String {
        return  Settings.Secure.getString(contentResolver,
                Settings.Secure.ANDROID_ID)
    }
}