package com.nieldeokar.hurumessenger.packets

import com.nieldeokar.hurumessenger.utils.NetworkUtils
import com.nieldeokar.hurumessenger.utils.Utils
import java.nio.ByteBuffer
import java.nio.charset.Charset

class MessagePacket {

    lateinit var msgBody: String
    lateinit var senderId: String
    lateinit var messageId: String
    var timeOfCreation = 0L

    var packetType : Int = 2

    fun setPacket(packet: ByteArray) {

        val byteBuffer = ByteBuffer.wrap(packet)

        packetType = Utils.toUnsignedInt(byteBuffer.get())
        timeOfCreation = byteBuffer.long

        val msgBodyLengthInBytes = Utils.toUnsignedInt(byteBuffer.get())
        val msgBodyAsBytes = ByteArray(msgBodyLengthInBytes)
        byteBuffer.get(msgBodyAsBytes)
        msgBody = String(msgBodyAsBytes, Charsets.UTF_8)

        val senderIdLengthInBytes = Utils.toUnsignedInt(byteBuffer.get())
        val senderIdAsBytes = ByteArray(senderIdLengthInBytes)
        byteBuffer.get(senderIdAsBytes)
        senderId = String(senderIdAsBytes, Charsets.UTF_8)


        val messageIdLengthInBytes = Utils.toUnsignedInt(byteBuffer.get())
        val messageIdAsBytes = ByteArray(messageIdLengthInBytes)
        byteBuffer.get(messageIdAsBytes)
        messageId = String(senderIdAsBytes, Charsets.UTF_8)

    }

    fun toByteArray(): ByteArray {

        val msgBodyAsBytesArray = NetworkUtils.removeExtraBytesFromString(msgBody, 500)
        val senderIdBytesArray =  senderId.toByteArray(Charsets.UTF_8)
        val messageIdBytesArray =  messageId.toByteArray(Charsets.UTF_8)

        val byteBuffer = ByteBuffer.allocate(1 + 8 + 1 + msgBodyAsBytesArray.size
                + 1 + senderIdBytesArray.size + 1 + messageIdBytesArray.size)

        byteBuffer.put(packetType.toByte()) // id
        byteBuffer.putLong(timeOfCreation)

        byteBuffer.put((msgBodyAsBytesArray.size.toByte()))
        byteBuffer.put(msgBodyAsBytesArray)

        byteBuffer.put(senderIdBytesArray.size.toByte())
        byteBuffer.put(senderIdBytesArray)

        byteBuffer.put(messageIdBytesArray.size.toByte())
        byteBuffer.put(messageIdBytesArray)

        return byteBuffer.array()
    }
}