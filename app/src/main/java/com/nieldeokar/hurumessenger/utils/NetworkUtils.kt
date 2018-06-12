package com.nieldeokar.hurumessenger.utils

import timber.log.Timber
import java.net.Inet4Address
import java.net.NetworkInterface
import java.net.SocketException
import java.net.UnknownHostException

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

}