package com.nieldeokar.hurumessenger.generator

import com.nieldeokar.hurumessenger.HuruApp
import com.nieldeokar.hurumessenger.packets.LocalAddressCard
import com.nieldeokar.hurumessenger.packets.MePacket
import com.nieldeokar.hurumessenger.services.LocalTransport
import com.nieldeokar.hurumessenger.utils.NetworkUtils

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
}