package com.nieldeokar.hurumessenger.services;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.HandlerThread;

import com.nieldeokar.hurumessenger.generator.PacketGenerator;
import com.nieldeokar.hurumessenger.models.Contact;
import com.nieldeokar.hurumessenger.packets.AddressCardV2;
import com.nieldeokar.hurumessenger.utils.NetworkUtils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Arrays;

import timber.log.Timber;

public final class LocalPacketTransport {

    public static int listeningPort = 0;

    private static void setBroadCastPort(int broadCastPort) {
        LocalPacketTransport.broadCastPort = broadCastPort;
    }

    private static int broadCastPort = 9418;

    private static final int broadcastPorts[] = new int[]{9418, 6666, 44556};

    private DatagramSocket socket;
    private DatagramSocket probeSocket;
    private Thread localSocketThread;
    private Thread broadCastThread;
    private HandlerThread handlerThread;
    private Handler handler;
    private WifiManager.MulticastLock mLock;
    private Context mContext;
    private PacketGenerator packetGenerator;

    public LocalPacketTransport(Context context) throws LocalTransportException {

        try {
            this.mContext = context;
            packetGenerator = new PacketGenerator();
            socket = new DatagramSocket(new InetSocketAddress(InetAddress.getByName("0.0.0.0"), 0));
            listeningPort = socket.getLocalPort();
            Timber.e("LocalTransport is listening on 0.0.0.0:%d", listeningPort);
        } catch (IOException e) {
            throw new LocalTransportException("Failed to obtain UDP socket", e);
        }


        for (int port : broadcastPorts) {
            try {
                probeSocket = new DatagramSocket(new InetSocketAddress(InetAddress.getByAddress(new byte[4]), port));
                probeSocket.setBroadcast(true);
                setBroadCastPort(port);
                return;

            } catch (IOException e) {
                if (probeSocket != null) probeSocket.close();
                probeSocket = null;
                Timber.e("Unable to bind %d as broadcast port", port);
                Timber.e(e);

            }
        }
        Timber.e("Failed to bind any of the broadcast port numbers");


    }

    static class LocalTransportException extends Exception {
        LocalTransportException(String message, Exception e) {
            super(message, e);
        }
    }

    public LocalPacketTransport start() {
        localSocketThread = new Thread(() -> listenerLoop(socket), "LTReceiver");
        localSocketThread.start();
        handlerThread = new HandlerThread("LTSender");
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());

        if (probeSocket != null) {
            broadCastThread = new Thread(() -> listenerLoop(probeSocket), "LTBroadcast");
            broadCastThread.start();
        }


        WifiManager wifi;
        wifi = (WifiManager) mContext.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifi != null) {
            mLock = wifi.createMulticastLock("lock");
            mLock.acquire();
        }

        sendIAMPacketNow();

        //router = new UnPnPRouter();
        //router.discoverUnPnPDevices();
        return this;
    }

    private long getLastIAMSentTime() {
        return lastIAMSentTime;
    }

    private void setLastIAMSentTime(long lastIAMSentTime) {
        this.lastIAMSentTime = lastIAMSentTime;
    }

    private volatile long lastIAMSentTime = 0; //This variable need to be volatile because it will be read by two threads and modified by one thread

    public void onUserActive() {
        if ((System.currentTimeMillis() - getLastIAMSentTime()) > 15_000) {
            sendIAMPacketNow();
        }
    }

    public void onDeviceIdle() {
        handler.postDelayed(powerSavingRunnable, 30_000);
    }

    public void onNewNetworkConnected()
    {
        for (int i = 0; i < 3; i++)
        sendIAMPacketNow();
    }

    synchronized public void onDeviceInteractive() {
        handler.removeCallbacks(powerSavingRunnable);
        if (mLock != null && !mLock.isHeld()) {
            mLock.acquire();
            Timber.i("Wifi multi-cast lock acquired");
            /*if (router == null) {
                router = new UnPnPRouter();
                router.discoverUnPnPDevices();
            }*/
        }
        sendIAMPacketNow();
    }

    private Runnable powerSavingRunnable = this::goPowerSaving;

    synchronized private void goPowerSaving() {
        if (mLock != null && mLock.isHeld()) {
            mLock.release();
            //router.shutdown();
            //router = null;
            Timber.i("Wifi multi-cast lock released");
        }
    }

   /* synchronized public InetSocketAddress getExternalSocketAddress(DatagramSocket datagramSocket) {
        if (router != null) {
            return router.mapPort(datagramSocket);
        } else {
            return null;
        }
    }*/


    private Runnable iAmSender = new Runnable() {
        @Override
        public void run() {

            Timber.i("Sending Me Packet");
            byte packetData[] = packetGenerator.generateIamPacket();
            for (int port : broadcastPorts) {
                DatagramPacket packet = new DatagramPacket(packetData, packetData.length, new InetSocketAddress("255.255.255.255", port));
                sendBroadCastDataGram(packet);
            }
            setLastIAMSentTime(System.currentTimeMillis());
            handler.postDelayed(iAmSender, 600_000);
        }
    };


    private boolean hasAbroadcastableNetwork() {
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                Network activeNetwork = cm.getActiveNetwork();
                if(activeNetwork==null) return false;
                NetworkCapabilities networkCapabilities = cm.getNetworkCapabilities(activeNetwork);
                return networkCapabilities != null && (!networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN));
            } else {
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                return activeNetwork.getTypeName() != null && !activeNetwork.getTypeName().equalsIgnoreCase("MOBILE");
            }
        }
        return false;
    }

    public void stop() {
        if (handlerThread != null && handlerThread.isAlive()) handlerThread.quit();
        if (localSocketThread != null && localSocketThread.isAlive()) localSocketThread.interrupt();
        if (broadCastThread != null && broadCastThread.isAlive()) broadCastThread.interrupt();
        if (socket != null) socket.close();
        if (probeSocket != null) probeSocket.close();
        if (mLock != null) mLock.release();
        if (mLock != null && mLock.isHeld()) mLock.release();
    }

    private void listenerLoop(DatagramSocket socket) {

        int retryCount = 0;

        DatagramPacket packet = new DatagramPacket(new byte[2048], 2048);

        while (!Thread.interrupted() && !socket.isClosed()) {
            retryCount++;
            try {
                socket.receive(packet);
                InetAddress address = packet.getAddress();
                if (address.isLoopbackAddress() || address.isAnyLocalAddress() || address.equals(NetworkUtils.getLocalIpV4Address())) {
                    Timber.v("Ignoring loopback");
                    continue;
                }
                byte[] packetData = Arrays.copyOfRange(packet.getData(), 0, packet.getLength());

                int magicByte = packetData[0] % 9;

                switch (magicByte) {
                   /* case PacketType.CONTROL:
                    case PacketType.MESSAGE:
                        if (packetProcessor != null)
                            packetProcessor.onRawPacketReceived(packetData);

                        break;
*/
                    case 1:
                            Timber.i("Broadcast packet from %s", address.toString());
//                            packetProcessor.onRawPacketReceived(packetData);

                            if ((System.currentTimeMillis() - getLastIAMSentTime()) > 10_000) {
                                sendIAMPacketNow();
                            }

                        break;
                    default:
                        Timber.w("Received in unknown packet type %d on %s , from %s. Packet size %d", magicByte, Thread.currentThread().getName(), address.toString(), packetData.length);
                        break;
                }
                retryCount = 0;
            } catch (IOException e) {
                Timber.e("IOException on localTransportListener: %s", Thread.currentThread().getName());
                Timber.e(e);
                if (retryCount > 100) {
                    Timber.e("LocalTransport listener thread %s encountered IOException %d times in a row. Exiting", Thread.currentThread().getName(), retryCount);
                    socket.close();
                    return;
                }

            }
        }
    }

    public void sendPacket(byte[] packetData, Contact contact) {
        if (contact.getAddressCard() == null) {
            Timber.d("Contact Extended address card is null");
            return;
        }
        AddressCardV2 addressCard = contact.getAddressCard();
        InetSocketAddress receiverSocketAddress = new InetSocketAddress(addressCard.getLocalV4Ip(), addressCard.getLocalV4Port());
        if (receiverSocketAddress != null && NetworkUtils.isValidNonSelfSocket(receiverSocketAddress)) {
            DatagramPacket packet = new DatagramPacket(packetData, packetData.length, receiverSocketAddress);
            Timber.d("Publish local packet");
            handler.post(() -> sendDataGram(packet));
        } else {
            Timber.d("Invalid destination for local transport. Ignoring");
        }

    }

    private void sendIAMPacketNow() {
        handler.removeCallbacks(iAmSender);
        handler.post(iAmSender);
    }

    private void sendDataGram(DatagramPacket datagramPacket) {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.send(datagramPacket);
            }
        } catch (IOException e) {
            Timber.e(e);
        }
    }

    private void sendBroadCastDataGram(DatagramPacket datagramPacket) {
        if (hasAbroadcastableNetwork())
            try {
                if (probeSocket != null && !probeSocket.isClosed()) {
                    probeSocket.send(datagramPacket);
                } else if (socket != null && !socket.isClosed()) {
                    socket.send(datagramPacket);
                }
            } catch (IOException e) {
                Timber.e(e);
            }
        else
            Timber.e("Not on a network that support broadcasts");
    }
}
