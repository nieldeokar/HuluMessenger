package com.nieldeokar.hurumessenger.packets

import com.nieldeokar.hurumessenger.utils.NetworkUtils
import com.nieldeokar.hurumessenger.utils.Utils
import java.nio.ByteBuffer
import java.nio.charset.Charset

class MePacket {

    lateinit var localAddressCard: LocalAddressCard
    lateinit var name: String
    lateinit var deviceId: String
    var packetType : Int = 0


    var size = LocalAddressCard.size


    fun setPacket(packet: ByteArray) {
        val byteBuffer = ByteBuffer.wrap(packet)

        packetType = Utils.toUnsignedInt(byteBuffer.get())

        val localAddressCardBytes = ByteArray(LocalAddressCard.size)
        byteBuffer.get(localAddressCardBytes,0,localAddressCardBytes.size)

        val userNameLengthInBytes = Utils.toUnsignedInt(byteBuffer.get())
        val userNameAsBytes = ByteArray(userNameLengthInBytes)
        byteBuffer.get(userNameAsBytes)
        name = String(userNameAsBytes, Charsets.UTF_8)

        val deviecIdLengthInBytes = Utils.toUnsignedInt(byteBuffer.get())
        val deviceIdAsBytes = ByteArray(deviecIdLengthInBytes )
        byteBuffer.get(deviceIdAsBytes )
        deviceId = String(deviceIdAsBytes, Charsets.UTF_8)


        localAddressCard = LocalAddressCard(localAddressCardBytes)

    }

    fun toByteArray(): ByteArray {
        val userNameAsBytesArray = NetworkUtils.removeExtraBytesFromString(name, 50)
        val deviceIdBytesArray =  deviceId.toByteArray(Charsets.UTF_8)

        val byteBuffer = ByteBuffer.allocate(1 + size + 1 + userNameAsBytesArray.size + 1 + deviceIdBytesArray.size)
        byteBuffer.put(1.toByte()) // id
        byteBuffer.put(localAddressCard.toBytes())

        byteBuffer.put((userNameAsBytesArray.size.toByte()))
        byteBuffer.put(userNameAsBytesArray)

        byteBuffer.put(deviceIdBytesArray.size.toByte())
        byteBuffer.put(deviceIdBytesArray)

        return byteBuffer.array()
    }
}