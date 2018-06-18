package com.nieldeokar.hurumessenger.models

import android.os.Parcel
import android.os.Parcelable
import com.nieldeokar.hurumessenger.HuruApp

/**
 * Created by nieldeokar on 16/06/18.
 */

class Message(var msgId : String = "", var textBody : String = "",
              var timeOfCreation : Long = 0L, var senderId : String = "") : Parcelable {

    fun isOutgoingMessage(): Boolean{
        return this.senderId == HuruApp.appInstance.getAccount()?.devicId
    }

    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readLong(),
            parcel.readString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(msgId)
        parcel.writeString(textBody)
        parcel.writeLong(timeOfCreation)
        parcel.writeString(senderId)
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