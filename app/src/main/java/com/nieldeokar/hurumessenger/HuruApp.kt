package com.nieldeokar.hurumessenger

import android.app.Application
import android.util.Log
import timber.log.Timber

class HuruApp : Application() {

    companion object {

        lateinit var appInstance : HuruApp
        lateinit var userName : String
    }

    override fun onCreate() {
        super.onCreate()
        appInstance = this
        userName = "User1"

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
}