package com.nieldeokar.hurumessenger.ui

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.nieldeokar.hurumessenger.R
import com.nieldeokar.hurumessenger.services.LocalTransport

class MainActivity : AppCompatActivity() {

    val localTransport = LocalTransport()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 1. Collect Name n local address
        // 2. Broadcast it
        // 3. Prepare listener
        // 4. Process received packet on listener


        localTransport.initialise()
    }


    override fun onDestroy() {
        super.onDestroy()
        localTransport.stop()
    }
}
