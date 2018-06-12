package com.nieldeokar.hurumessenger.utils

import timber.log.Timber
import java.net.*
import java.security.InvalidParameterException
import kotlin.experimental.and

object NetworkUtils {

    @JvmStatic
    fun getLocalIpV4Address(): Inet4Address? {
        var ipAddress: Inet4Address? = null
        try {
            ipAddress = Inet4Address.getByAddress(byteArrayOf(0, 0, 0, 0)) as Inet4Address
            val enumNetworkInterfaces = NetworkInterface.getNetworkInterfaces()
            while (enumNetworkInterfaces.hasMoreElements()) {
                val networkInterface = enumNetworkInterfaces
                        .nextElement()
                val enumInetAddress = networkInterface
                        .inetAddresses
                while (enumInetAddress.hasMoreElements()) {
                    val inetAddress = enumInetAddress.nextElement()

                    if (!inetAddress.isLoopbackAddress && inetAddress is Inet4Address) {
                        Timber.i("Local Ip Address: " + inetAddress.getHostAddress() + "\n");
                        ipAddress = inetAddress
                    }
                }
            }
        } catch (e: SocketException) {
            Timber.e(e)
        } catch (e: UnknownHostException) {
            Timber.e(e)
        }

        return ipAddress
    }


    @JvmStatic
    fun isValidNonSelfSocket(socketAddress: InetSocketAddress): Boolean {
        val inetAddress = socketAddress.address
        return try {
            !(socketAddress.port <= 0 || socketAddress.port > 65535 ||
                    inetAddress.isMulticastAddress ||
                    inetAddress.isAnyLocalAddress ||
                    inetAddress == InetAddress.getByName("0.0.0.0"))
        } catch (ignored: UnknownHostException) {
            false
        }

    }

}