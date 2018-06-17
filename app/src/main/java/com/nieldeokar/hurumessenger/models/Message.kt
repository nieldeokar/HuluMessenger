package com.nieldeokar.hurumessenger.models

import android.os.Parcelable
import com.nieldeokar.hurumessenger.HuruApp
import kotlinx.android.parcel.Parcelize

/**
 * Created by nieldeokar on 16/06/18.
 */

@Parcelize
class Message : Parcelable {

    var msgId : String = ""

    var textBody : String = ""

    var timeOfCreation = 0L

    var senderId : String = ""

    fun isOutgoingMessage(): Boolean{
        return this.senderId == HuruApp.appInstance.getAccount()?.devicId
    }
}