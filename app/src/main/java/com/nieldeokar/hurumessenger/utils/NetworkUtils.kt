package com.nieldeokar.hurumessenger.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import timber.log.Timber
import java.net.*
import java.nio.ByteBuffer
import java.nio.charset.Charset
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
                        Timber.i("Local Ip Address: " + inetAddress.getHostAddress() + "\n")
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

    @JvmStatic
    fun hasAbroadcastableNetwork(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (cm != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val activeNetwork = cm.activeNetwork ?: return false
                val networkCapabilities = cm.getNetworkCapabilities(activeNetwork)
                return networkCapabilities != null && (!networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN))
            } else {
                val activeNetwork = cm.activeNetworkInfo
                return activeNetwork.typeName != null && !activeNetwork.typeName.equals("MOBILE", ignoreCase = true)
            }
        }
        return false
    }

    @JvmStatic
    fun removeExtraBytesFromString(data: String, maxLength: Int): ByteArray {
        val utf8CharSet = Charset.forName("UTF-8")
        val utf8String = data.toByteArray(utf8CharSet)
        if (utf8String.size <= maxLength)
            return utf8String
        else {
            val buffer = ByteBuffer.allocate(maxLength)
            val originalStringLength = data.length
            var i = originalStringLength
            while (i > 0) {
                val j = data.substring(i - 1, i).toByteArray(utf8CharSet)
                if (buffer.remaining() >= j.size) {
                    buffer.put(j)
                } else {
                    break
                }
                i++
            }
            return buffer.array()

        }
    }

}