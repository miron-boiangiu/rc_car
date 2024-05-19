package com.teste.bluetoothclient;

import javax.bluetooth.*;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class BluetoothClient {
    private final Object lock = new Object(); // Used for synchronizing with the DiscoveryListener
    private StreamConnection connection = null;
    private DataOutputStream outputStream;
    private DataInputStream inputStream;
    private final LocalDevice localDevice;

    protected RemoteDevice targetDevice = null;
    protected ServiceRecord rfcommService = null;

    public BluetoothClient(String deviceIdentifier) throws IOException {

        SPPDiscoveryListener discoveryListener = new SPPDiscoveryListener(lock, deviceIdentifier, this);

        localDevice = LocalDevice.getLocalDevice();
        DiscoveryAgent agent = localDevice.getDiscoveryAgent();
        agent.startInquiry(DiscoveryAgent.GIAC, discoveryListener);

        try {
            synchronized(lock){
                lock.wait();
            }
        }
        catch (InterruptedException e) {
            throw new IOException(e);
        }

        if (targetDevice == null) {
            throw new IOException("Can't find target device.");
        }

        UUID[] uuidSet = new UUID[1];
        uuidSet[0]=new UUID(0x0003); //RFCOMM service

        agent.searchServices(null, uuidSet, targetDevice, discoveryListener);

        try {
            synchronized(lock){
                lock.wait();
            }
        }
        catch (InterruptedException e) {
            throw new IOException(e);
        }

        if (rfcommService == null) {
            throw new IOException("No RFCOMM service found on the target device.");
        }

        connection = (StreamConnection) Connector.open(rfcommService
                .getConnectionURL(ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false));

        inputStream = connection.openDataInputStream();
        outputStream = connection.openDataOutputStream();

        System.out.println("Bluetooth ready.");
    }

    public void sendData(byte[] data) throws IOException {
        outputStream.write(data);
    }

    public void sendByte(byte data) throws IOException {
        outputStream.writeByte(data);
    }

    public boolean isDataAvailable() throws IOException {
        return inputStream.available() > 0;
    }

    public int availableDataSize() throws IOException {
        return inputStream.available();
    }

    public int getData(int len, byte[] arr) throws IOException {
        return inputStream.read(arr, 0, len);
    }

    public byte getByte() throws IOException {
        return (byte)inputStream.read();
    }

    public byte[] getData() throws IOException {
        int availableDataSize = inputStream.available();
        byte[] readBytes = new byte[availableDataSize];
        int res = inputStream.read(readBytes);
        return readBytes;
    }
}

