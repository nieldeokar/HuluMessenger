package com.nieldeokar.hurumessenger.services

import android.app.Service
import android.content.Intent
import android.os.IBinder

class HuruService : Service() {

    companion object {
        lateinit var mHuruService : HuruService
    }

    lateinit var localTransport : LocalTransport

    override fun onCreate() {
        super.onCreate()
        localTransport = LocalTransport()
        mHuruService = this
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }
}
