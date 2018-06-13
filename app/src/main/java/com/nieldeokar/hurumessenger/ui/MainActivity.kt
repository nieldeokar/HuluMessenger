package com.nieldeokar.hurumessenger.ui

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.nieldeokar.hurumessenger.HuruApp
import com.nieldeokar.hurumessenger.R
import com.nieldeokar.hurumessenger.database.entity.UserEntity
import com.nieldeokar.hurumessenger.generator.PacketGenerator
import com.nieldeokar.hurumessenger.packets.MePacket
import com.nieldeokar.hurumessenger.services.LocalTransport
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber
import java.util.concurrent.Executors
import android.provider.Settings.Secure
import com.nieldeokar.hurumessenger.database.entity.AccountEntity


class MainActivity : AppCompatActivity(), LocalTransport.OnMePacketReceivedListener {

    lateinit var localTransport : LocalTransport

    val packetGenerator = PacketGenerator()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 1. Collect Name n local address
        // 2. Broadcast it
        // 3. Prepare listener
        // 4. Process received packet on listener

        localTransport = LocalTransport()
        localTransport.setOnMePacketReceivedListener(this)
        localTransport.initialise()

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.addItemDecoration(MyDividerItemDecoration(this, LinearLayoutManager.VERTICAL, 16))


        val list = ArrayList<UserEntity>()
        recyclerView.adapter = UsersAdapter(list)

        recyclerView.itemAnimator = DefaultItemAnimator()

        val executor = Executors.newSingleThreadExecutor()

        executor.execute({
            val accounts = (application as HuruApp).getDatabase()?.accountDao()?.all

            if(accounts?.size != 0){

                localTransport.sendMePacketNow()

                val users = (application as HuruApp).getDatabase()?.usersDao()?.all

                if(users?.size != 0){
                    runOnUiThread({
                        recyclerView.adapter = UsersAdapter(users)
                    })
                }

            }else{
                Timber.d("No account object found")

            }
        })

        recyclerView.addOnItemTouchListener(RecyclerTouchListener(applicationContext, recyclerView, object : RecyclerTouchListener.ClickListener {
            override fun onClick(view: View?, position: Int) {

            }

            override fun onLongClick(view: View?, position: Int) {

            }
        }))

    }

    fun send(view : View){
        val usrName = etUserName.text.toString()
        if(usrName.isNotBlank() && usrName.isNotEmpty()){
            HuruApp.userName = usrName

            val account = AccountEntity()
            account.device_id = (application as HuruApp).getDeviceID()
            account.name = usrName
            account.mePacket = PacketGenerator().generateMePacket()

            val executor = Executors.newSingleThreadExecutor()

            executor.execute({
                (application as HuruApp).getDatabase()?.accountDao()?.insertAccount(account)
            })

            localTransport.sendMePacketNow()
        }
    }

    override fun onMePacketReceived(mePacket: MePacket) {
        val str = "mePacketReceived from : ${mePacket.name} \n"
        Timber.d(str)
        val user = UserEntity()
        user.device_id = mePacket.deviceId
        user.mePacket = mePacket.toByteArray()
        user.name = mePacket.name

        val executor = Executors.newSingleThreadExecutor()

        executor.execute({
            (application as HuruApp).getDatabase()?.usersDao()?.insertUser(user)
        })

        runOnUiThread({
            ((recyclerView.adapter) as UsersAdapter).addUser(user)
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        localTransport.stop()
    }
}
