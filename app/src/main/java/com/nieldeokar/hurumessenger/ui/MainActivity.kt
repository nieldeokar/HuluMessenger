package com.nieldeokar.hurumessenger.ui

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.nieldeokar.hurumessenger.R
import com.nieldeokar.hurumessenger.packets.LocalAddressCard
import com.nieldeokar.hurumessenger.services.LocalPacketTransport
import com.nieldeokar.hurumessenger.utils.NetworkUtils
import com.nieldeokar.hurumessenger.utils.Utils
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    lateinit var localPacketTransport : LocalPacketTransport

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 1. Collect Name n local address
        // 2. Broadcast it
        // 3. Prepare listener
        // 4. Process received packet on listener

        localPacketTransport = LocalPacketTransport(this)
        localPacketTransport.start()

        val localAddressCard = LocalAddressCard()
        localAddressCard.localV4Address = NetworkUtils.getLocalIpV4Address()!!
        localAddressCard.localV4Port = LocalPacketTransport.listeningPort

        Timber.d("local v4 addreess ${localAddressCard.localV4Address.hostAddress}")
        Timber.d("local port ${LocalPacketTransport.listeningPort}")
    }
}
