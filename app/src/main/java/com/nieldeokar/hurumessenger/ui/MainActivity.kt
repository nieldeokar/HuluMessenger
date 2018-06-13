package com.nieldeokar.hurumessenger.ui

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.nieldeokar.hurumessenger.HuruApp
import com.nieldeokar.hurumessenger.R
import com.nieldeokar.hurumessenger.packets.MePacket
import com.nieldeokar.hurumessenger.services.HuruService
import com.nieldeokar.hurumessenger.services.LocalTransport
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber

class MainActivity : AppCompatActivity(), LocalTransport.OnMePacketReceivedListener {
    lateinit var localTransport : LocalTransport

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

    }

    fun send(view : View){
        val usrName = etUserName.text.toString()
        if(usrName.isNotBlank() && usrName.isNotEmpty()){
            HuruApp.userName = usrName
            localTransport.sendMePacketNow()
        }
    }

    override fun onMePacketReceived(mePacket: MePacket) {
        val str = "mePacketReceived from : ${mePacket.name} \n"
        Timber.d(str)
        runOnUiThread({
            tvLogs.append(str)
        })
    }


    override fun onDestroy() {
        super.onDestroy()
        localTransport.stop()
    }
}
