package com.nieldeokar.hurumessenger.packets

import com.nieldeokar.hurumessenger.utils.Utils
import java.net.Inet4Address
import java.net.Inet6Address
import java.net.InetAddress
import java.net.UnknownHostException
import java.nio.ByteBuffer

class LocalAddressCard {

    companion object {
        const val size = 4 + 2 + 16 + 2
    }
    var localV4Address: Inet4Address
    var localV6Address: Inet6Address
    var localV4Port: Int = 0
    var localV6Port: Int = 0

    constructor() {
        try {
            localV4Address = InetAddress.getByAddress(ByteArray(4)) as Inet4Address
            localV6Address = InetAddress.getByAddress(ByteArray(16)) as Inet6Address
            localV4Port = 0
            localV6Port = 0
        } catch (e: UnknownHostException) {
            throw RuntimeException(e)
        }
    }


    constructor(packet: ByteArray) {
        if (packet.size != size) throw IllegalArgumentException()
        try {
            val byteBuffer = ByteBuffer.wrap(packet)

            val v4AddressBytes = ByteArray(4)
            byteBuffer.get(v4AddressBytes)
            localV4Address = InetAddress.getByAddress(v4AddressBytes) as Inet4Address
            val portBytes = ByteArray(2)
            byteBuffer.get(portBytes)
            localV4Port = Utils.networkEndianTwoBytesIntoUnsignedInteger(portBytes)
            val v6AddressBytes = ByteArray(16)
            byteBuffer.get(v6AddressBytes)
            localV6Address = InetAddress.getByAddress(v6AddressBytes) as Inet6Address
            byteBuffer.get(portBytes)
            localV6Port = Utils.networkEndianTwoBytesIntoUnsignedInteger(portBytes)
        } catch (e: UnknownHostException) {
            throw RuntimeException(e)
        }

    }

    fun toBytes(): ByteArray {
        val byteBuffer = ByteBuffer.allocate(size)
        byteBuffer.put(localV4Address.address)
        byteBuffer.put(Utils.unsignedShortIntegerIntoTwoBytes(localV4Port))
        byteBuffer.put(localV6Address.address)
        byteBuffer.put(Utils.unsignedShortIntegerIntoTwoBytes(localV6Port))
        return byteBuffer.array()
    }

}