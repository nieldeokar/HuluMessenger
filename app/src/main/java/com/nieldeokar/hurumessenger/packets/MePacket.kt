package com.nieldeokar.hurumessenger.packets

import java.nio.ByteBuffer

class MePacket {

    lateinit var localAddressCard: LocalAddressCard
    var size = LocalAddressCard.size


    fun setPacket(packet: ByteArray) {
        val byteBuffer = ByteBuffer.wrap(packet)
        val localAddressCardBytes = ByteArray(LocalAddressCard.size)
        byteBuffer.get(localAddressCardBytes)
        localAddressCard = LocalAddressCard(localAddressCardBytes)

    }

    fun toByteArray(): ByteArray {
        val byteBuffer = ByteBuffer.allocate(size + 1)
        byteBuffer.put(1.toByte()) // id
        byteBuffer.put(localAddressCard.toBytes())
        return byteBuffer.array()
    }
}