package com.nieldeokar.hurumessenger.models

import java.util.concurrent.CopyOnWriteArrayList


class Account{

    var aid: Int = 0

    var name: String = ""

    var devicId: String = ""

    var localAddressCard : ByteArray? = null

    val usersList = CopyOnWriteArrayList<User>()


    fun findUserByDeviceId(deviceid : String) : User? {

        for (user in usersList){
            if(user.deviceId == deviceid){
                return user
            }
        }
        return null
    }
}