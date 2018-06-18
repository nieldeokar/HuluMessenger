package com.nieldeokar.hurumessenger.models

import android.os.Parcel
import android.os.Parcelable
import java.util.ArrayList


class User(var uid : Int = 0,var name: String = "",var deviceId: String = "",
           var localAddressCard : ByteArray? = null,
           var lastActiveTime : Long = 0L,
           var messages :MutableList<Message> = ArrayList()) : Parcelable {


    fun findMessageById(id : String) : Message? {

        return messages.firstOrNull { it.msgId == id }
    }

    constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readString(),
            parcel.readString(),
            parcel.createByteArray(),
            parcel.readLong(),
            arrayListOf<Message>().apply {
                parcel.readList(this, Message::class.java.classLoader)
            })


    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(uid)
        parcel.writeString(name)
        parcel.writeString(deviceId)
        parcel.writeByteArray(localAddressCard)
        parcel.writeLong(lastActiveTime)
        parcel.writeList(messages)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }
}