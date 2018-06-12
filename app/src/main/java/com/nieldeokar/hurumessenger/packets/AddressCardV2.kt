package com.nieldeokar.hurumessenger.packets

import android.util.SparseArray
import com.nieldeokar.hurumessenger.utils.Utils
import java.io.ByteArrayOutputStream
import java.net.Inet4Address
import java.net.InetAddress
import java.nio.ByteBuffer
import java.util.*

class AddressCardV2 {

    var hashMapData: SparseArray<ByteArray> = SparseArray(14)

    var localV4Ip: Inet4Address = InetAddress.getByAddress(kotlin.ByteArray(4)) as Inet4Address //26 + 4 =30
    var localV4Port: Int = 0 // 30+2 = 32
    var localV6Ip: ByteArray = ByteArray(8) //32 + 8 = 40
    var localV6Port: ByteArray = ByteArray(4) // ??

    var size = 44

    constructor()

    constructor(bytes: ByteArray) {
        val byteBuffer = ByteBuffer.wrap(bytes)

        size = bytes.size

        while (byteBuffer.remaining() > 0) {
            val keyArray = ByteArray(1)
            val dataLengthArray = ByteArray(1)
            byteBuffer.get(keyArray)
            byteBuffer.get(dataLengthArray)

            val data = ByteArray(Utils.toUnsignedInt(dataLengthArray[0]))
            byteBuffer.get(data, 0, data.size)
            val key = Utils.toUnsignedInt(keyArray[0])
            hashMapData.put(key, data)

            when (key) {


                LOCAL_V4_IP -> localV4Ip = Inet4Address.getByAddress(data) as Inet4Address
                LOCAL_V4_PORT -> localV4Port = Utils.networkEndianTwoBytesIntoUnsignedInteger(data)

                LOCAL_V6_IP -> localV6Ip = data
                LOCAL_V6_PORT -> localV6Port = data

            }
        }


    }

    fun toByteArray(): ByteArray {


        hashMapData.put(LOCAL_V4_IP, localV4Ip.address)
        hashMapData.put(LOCAL_V4_PORT, Utils.unsignedShortIntegerIntoTwoBytes(localV4Port))

        hashMapData.put(LOCAL_V6_IP, localV6Ip)
        hashMapData.put(LOCAL_V6_PORT, localV6Port)

//        supportedFeatures = QTConnectApp.applicationInstance.account.supportedFeaturesList!!


        val byteArrayOutputStream = ByteArrayOutputStream()

        for (i in 0 until hashMapData.size()) {

            val key = hashMapData.keyAt(i)
            val value = hashMapData.valueAt(i)

            byteArrayOutputStream.write(Utils.unsignedShortIntegerIntoOneByte(key))
            byteArrayOutputStream.write(Utils.unsignedShortIntegerIntoOneByte(value.size))
            byteArrayOutputStream.write(value)
        }

        val finalByteArray = byteArrayOutputStream.toByteArray()

        val byteBuffer = ByteBuffer.allocate(finalByteArray.size)
                .put(finalByteArray)


        size = byteBuffer.array().size

        return byteBuffer.array()
    }



    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AddressCardV2

        if (!(localV4Ip.equals(localV4Ip))) return false
        if ((localV4Port != other.localV4Port)) return false
        if (!Arrays.equals(localV6Ip, other.localV6Ip)) return false
        if (!Arrays.equals(localV6Port, other.localV6Port)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = localV4Ip.hashCode()
        result = 31 * result + localV4Port
        result = 31 * result + Arrays.hashCode(localV6Ip)
        result = 31 * result + Arrays.hashCode(localV6Port)
        return result
    }

    override fun toString(): String {
        var abc: String = String()
        abc = abc + "\n" + localV4Ip.toString() + " : " + Integer.toString(localV4Port) + "\n"

        return abc
    }


    companion object {

        const val LOCAL_V4_IP = 1
        const val LOCAL_V4_PORT = 2

        const val LOCAL_V6_IP = 3
        const val LOCAL_V6_PORT = 4

        const val SUPPORTED_FEATURES = 10
    }
}