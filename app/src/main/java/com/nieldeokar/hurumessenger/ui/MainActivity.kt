package com.nieldeokar.hurumessenger.ui

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.nieldeokar.hurumessenger.HuruApp
import com.nieldeokar.hurumessenger.R
import com.nieldeokar.hurumessenger.generator.PacketGenerator
import com.nieldeokar.hurumessenger.packets.MePacket
import com.nieldeokar.hurumessenger.services.LocalTransport
import timber.log.Timber
import java.util.concurrent.Executors
import com.nieldeokar.hurumessenger.R.id.recyclerView
import com.nieldeokar.hurumessenger.database.entity.AccountEntity
import com.nieldeokar.hurumessenger.models.User
import java.util.ArrayList


class MainActivity : AppCompatActivity(), LocalTransport.OnMePacketReceivedListener {



    val list = ArrayList<User>()


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
        recyclerView.itemAnimator = DefaultItemAnimator()


        recyclerView.adapter = UsersAdapter(list)


        recyclerView.addOnItemTouchListener(RecyclerTouchListener(applicationContext, recyclerView, object : RecyclerTouchListener.ClickListener {
            override fun onClick(view: View?, position: Int) {

            }

            override fun onLongClick(view: View?, position: Int) {

            }
        }))

    }

    fun send(view: View) {
        val usrName = etUserName.text.toString()
        if (usrName.isNotBlank() && usrName.isNotEmpty()) {
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

        val user = (application as HuruApp).getAccount()?.findUserByDeviceId(mePacket.deviceId)
        user?.localAddressCard = mePacket.localAddressCard.toBytes()
        user?.name = mePacket.name

        runOnUiThread({

        })


    }

    override fun onDestroy() {
        super.onDestroy()
        localTransport.stop()
    }
}
