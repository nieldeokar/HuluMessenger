package com.nieldeokar.hurumessenger.database.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.PrimaryKey


/**
 * Created by @nieldeokar on 27/05/18.
 */


@Entity(tableName = "account")
class AccountEntity {

    @PrimaryKey(autoGenerate = true)
    var aid: Int = 0

    @ColumnInfo(name = "name")
    var name: String = ""

    @ColumnInfo(name = "device_id")
    var device_id: String = ""

    @ColumnInfo(name = "me_packet")
    var mePacket : ByteArray? = null

}