package com.nieldeokar.hurumessenger.generator

import com.nieldeokar.hurumessenger.HuruApp
import com.nieldeokar.hurumessenger.models.User
import com.nieldeokar.hurumessenger.packets.LocalAddressCard
import com.nieldeokar.hurumessenger.packets.MePacket
import com.nieldeokar.hurumessenger.packets.MessagePacket
import com.nieldeokar.hurumessenger.services.LocalTransport
import com.nieldeokar.hurumessenger.utils.NetworkUtils
import java.util.*

class PacketGenerator {

    fun generateMePacket(): ByteArray {

        val mePacket = MePacket()

        val localAddressCard = LocalAddressCard()
        localAddressCard.localV4Address = NetworkUtils.getLocalIpV4Address()!!
        localAddressCard.localV4Port = LocalTransport.listeningPort

        mePacket.localAddressCard = localAddressCard
        mePacket.name = HuruApp.account.name
        mePacket.deviceId = HuruApp.appInstance.getDeviceID()


        return mePacket.toByteArray()
    }

    fun generateMessagePacket(user: User, message : String) : ByteArray {
        val messagePacket = MessagePacket()

        messagePacket.messageId = UUID.randomUUID().toString()
        messagePacket.senderId = user.deviceId
        messagePacket.msgBody = message
        messagePacket.timeOfCreation = System.currentTimeMillis()
        messagePacket.packetType = 2


        return messagePacket.toByteArray()
    }
}