package com.nieldeokar.hurumessenger.services

import android.os.Handler
import com.nieldeokar.hurumessenger.HuruApp
import com.nieldeokar.hurumessenger.generator.PacketGenerator
import com.nieldeokar.hurumessenger.models.User
import com.nieldeokar.hurumessenger.packets.LocalAddressCard
import com.nieldeokar.hurumessenger.packets.MePacket
import com.nieldeokar.hurumessenger.packets.MessagePacket
import com.nieldeokar.hurumessenger.utils.NetworkUtils
import com.nieldeokar.hurumessenger.utils.Utils
import timber.log.Timber
import java.io.IOException
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by nieldeokar on 12/06/18.
 */
class LocalTransport {

    private var localSocket : DatagramSocket? = null
    private var broadcastSocket : DatagramSocket? = null

    companion object {
        var listeningPort = 0
    }
    val packetGenerator = PacketGenerator()

    private var myHandlerThread = MyHandlerThread("SenderThread")
    var handler : Handler? = null
    var mePacketSenderRunnable = MePacketSenderRunnable()

    private lateinit var localSocketThread : Thread
    private lateinit var localBroadCastThread : Thread

    private val broadcastPorts = intArrayOf(33445,9128,8888)
    private var hasBroadcastableNetwork = false

    private var onPacketReceivedListener: OnPacketReceivedListener? = null

    @Volatile private var lastSentTime = 0L


    constructor(){
        try {
            localSocket = DatagramSocket(InetSocketAddress(InetAddress.getByName("0.0.0.0"), 0))
            if(localSocket != null) listeningPort = localSocket?.localPort!!
            Timber.i("LocalTransport is listening on 0.0.0.0:%d", listeningPort)

            hasBroadcastableNetwork = NetworkUtils.hasAbroadcastableNetwork(HuruApp.appInstance)

            for (port in broadcastPorts) {
                try {
                    broadcastSocket = DatagramSocket(InetSocketAddress(InetAddress.getByAddress(ByteArray(4)), port))
                    broadcastSocket?.broadcast = true
                    return

                } catch (e: IOException) {
                    broadcastSocket?.close()
                    broadcastSocket = null
                    Timber.e("Did not bind to %d as broadcast port", port)
                    Timber.e(e)
                }
            }

            Timber.e("Unable to bind to any of port ")


        } catch (e: IOException) {
            Timber.e("Failed to obtain socket",e)
        }
    }

    fun setOnPacketReceivedListener(on : OnPacketReceivedListener?){
        this.onPacketReceivedListener = on
    }

    fun initialise() {
        myHandlerThread.start()
        handler = Handler(myHandlerThread.looper)

        if(localSocket != null) {
            localSocketThread = Thread(LocalListenerRunnable(localSocket!!))
            localSocketThread.start()
        }
        if(broadcastSocket != null) {
            localBroadCastThread = Thread(LocalListenerRunnable(broadcastSocket!!))
            localBroadCastThread.start()
        }
    }

    fun sendMePacketNow() {
        handler?.removeCallbacks(mePacketSenderRunnable)
        handler?.post(mePacketSenderRunnable)
    }

    fun sendMessagePacket(user: User,message : String){
        val messagePacket = packetGenerator.generateMessagePacket(user,message)

        if (user.localAddressCard == null) {
            Timber.d("User address card is null")
            return
        }
        val addressCard = LocalAddressCard(user.localAddressCard!!)
        val receiverSocketAddress = InetSocketAddress(addressCard.localV4Address, addressCard.localV4Port)
        if (receiverSocketAddress != null && NetworkUtils.isValidNonSelfSocket(receiverSocketAddress)) {
            val packet = DatagramPacket(messagePacket, messagePacket.size, receiverSocketAddress)
            Timber.d("Publish local packet")
            handler?.post { sendDataGram(packet) }
        } else {
            Timber.d("Invalid destination for local transport. Ignoring")
        }
    }

    private fun sendDataGram(datagramPacket: DatagramPacket) {
        try {
            if (localSocket != null && !localSocket!!.isClosed) {
                localSocket!!.send(datagramPacket)
            }
        } catch (e: IOException) {
            Timber.e(e)
        }

    }

    private fun setLastSentTime(lastSentTime : Long) {
        this.lastSentTime = lastSentTime
    }

    private fun getLastSentTime() : Long{
        return lastSentTime
    }

    fun stop() {
        if (myHandlerThread.isAlive) myHandlerThread.quit()
        if (localSocketThread != null && localSocketThread.isAlive) localSocketThread.interrupt()
        if (localBroadCastThread != null && localBroadCastThread.isAlive) localBroadCastThread.interrupt()
        if (localSocket != null) localSocket?.close()
        if (broadcastSocket != null) broadcastSocket?.close()
    }


    inner class MePacketSenderRunnable : Runnable {
        override fun run() {

            val packetData = packetGenerator.generateMePacket()

            broadcastPorts
                    .map { DatagramPacket(packetData, packetData.size, InetSocketAddress("255.255.255.255", it)) }
                    .forEach { sendBroadcast(it) }

            setLastSentTime(System.currentTimeMillis())

            handler?.postDelayed(mePacketSenderRunnable, TimeUnit.MINUTES.toMillis(30))
        }
    }

    private fun sendBroadcast(datagramPacket: DatagramPacket) {
        if (hasBroadcastableNetwork)
            try {
                if (localSocket != null && !localSocket?.isClosed!!) {
                    localSocket?.send(datagramPacket)
                } else if (broadcastSocket != null && !broadcastSocket?.isClosed!!) {
                    broadcastSocket?.send(datagramPacket)
                }
            } catch (e: IOException) {
                Timber.e(e)
            }
        else
            Timber.e("Network does not support broadcast")
    }


    inner class LocalListenerRunnable() : Runnable {

        private lateinit var localSocket : DatagramSocket

        constructor(localSocket : DatagramSocket) : this() {
            this.localSocket = localSocket
        }

        override fun run() {
            var retryCount = 0

            val packet = DatagramPacket(ByteArray(2048), 2048)

            while (!Thread.interrupted() && !localSocket.isClosed) {
                retryCount++
                try {
                    localSocket.receive(packet)
                    val address = packet.address
                    if (address.isLoopbackAddress || address.isAnyLocalAddress || address == NetworkUtils.getLocalIpV4Address()) {
                        Timber.v("Ignoring own packet")
                        continue
                    }
                    val packetData = Arrays.copyOfRange(packet.data, 0, packet.length)
                    val byteBuffer = ByteBuffer.wrap(packetData)
                    val type = Utils.toUnsignedInt(byteBuffer.get())

                    Timber.i("Received broadcast packet from %s", address.toString())

                    when (type) {
                        1 -> {

                            val mePacket = MePacket()
                            mePacket.setPacket(packetData)


                            Timber.i("packetAddress " + mePacket.localAddressCard.localV4Address.toString())
                            Timber.i("packetName " + mePacket.name)
                            if (System.currentTimeMillis() - getLastSentTime() > 1000) {
                                sendMePacketNow()
                            }

                            onPacketReceivedListener?.onMePacketReceived(mePacket)
                        }
                        2 ->{
                            val messagePacket = MessagePacket()
                            messagePacket.setPacket(packetData)

                            onPacketReceivedListener?.onMessagePacketReceived(messagePacket)
                        }
                        else -> Timber.w("Received in unknown packet type %d on %s , from %s. Packet size %d", type, Thread.currentThread().name, address.toString(), packetData.size)
                    }
                    retryCount = 0
                } catch (e: IOException) {
                    Timber.e("IOException on localListener: %s", Thread.currentThread().name)
                    Timber.e(e)
                    if (retryCount > 100) {
                        Timber.e("LocalListener thread %s encountered IOException %d times in a row. Exiting", Thread.currentThread().name, retryCount)
                        localSocket.close()
                        return
                    }
                }
            }
        }
    }

    interface OnPacketReceivedListener {
        fun onMePacketReceived(mePacket: MePacket)
        fun onMessagePacketReceived(messagePacket : MessagePacket)
    }
}