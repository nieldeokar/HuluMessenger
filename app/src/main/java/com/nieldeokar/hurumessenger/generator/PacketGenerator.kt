package com.nieldeokar.hurumessenger.generator

import com.nieldeokar.hurumessenger.packets.LocalAddressCard
import com.nieldeokar.hurumessenger.packets.MePacket
import com.nieldeokar.hurumessenger.services.LocalPacketTransport
import com.nieldeokar.hurumessenger.utils.NetworkUtils

class PacketGenerator {

    constructor()

    fun generateIamPacket(): ByteArray {


        val iamPacket = MePacket()


        val localAddressCard = LocalAddressCard()
        localAddressCard.localV4Address = NetworkUtils.getLocalIpV4Address()!!
        localAddressCard.localV4Port = LocalPacketTransport.listeningPort

        iamPacket.localAddressCard = localAddressCard


        return iamPacket.toByteArray()

    }
}