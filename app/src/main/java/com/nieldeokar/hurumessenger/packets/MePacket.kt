package com.nieldeokar.hurumessenger.packets

import java.nio.ByteBuffer
import java.nio.charset.Charset

class MePacket {

    lateinit var localAddressCard: LocalAddressCard
    lateinit var name: String

    var size = LocalAddressCard.size


    fun setPacket(packet: ByteArray) {
        val byteBuffer = ByteBuffer.wrap(packet)

        val localAddressCardBytes = ByteArray(LocalAddressCard.size)
        byteBuffer.get(localAddressCardBytes,0,localAddressCardBytes.size)

        val nameBytes = ByteArray(6)
        byteBuffer.get(nameBytes,0,nameBytes.size)
        name = String(nameBytes,Charset.defaultCharset())

        localAddressCard = LocalAddressCard(localAddressCardBytes)

    }

    fun toByteArray(): ByteArray {
        val byteBuffer = ByteBuffer.allocate(size + 1 + 6)
        byteBuffer.put(1.toByte()) // id
        byteBuffer.put(localAddressCard.toBytes())
        byteBuffer.put(name.toByteArray(Charset.defaultCharset()))
        return byteBuffer.array()
    }
}