package com.nieldeokar.hurumessenger.models

import android.annotation.SuppressLint
import android.os.Parcel
import android.os.Parcelable
import com.nieldeokar.hurumessenger.packets.LocalAddressCard
import kotlinx.android.parcel.Parcelize
import java.util.ArrayList




@Parcelize
class User(var uid : Int = 0,var name: String = "",var deviceId: String = "",
           var localAddressCard : ByteArray? = null,
           var lastActiveTime : Long = 0L,
           var messages :ArrayList<Message> = ArrayList()) : Parcelable {


    fun findMessageById(id : String) : Message? {

        return messages.firstOrNull { it.msgId == id }
    }

    constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readString(),
            parcel.readString(),
            parcel.readByteArray(LocalAddressCard()),
            parcel.readArrayList()
            )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(uid)
        parcel.writeString(name)
        parcel.writeString(deviceId)
        parcel.writeString(email)
        parcel.writeLong(phone)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Person> {
        override fun createFromParcel(parcel: Parcel): Person {
            return Person(parcel)
        }

        override fun newArray(size: Int): Array<Person?> {
            return arrayOfNulls(size)
        }
    }
}