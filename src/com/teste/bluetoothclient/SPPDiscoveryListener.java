package com.teste.bluetoothclient;

import javax.bluetooth.*;
import java.io.IOException;

public class SPPDiscoveryListener implements DiscoveryListener {
    private final Object lock;
    private final String deviceIdentifier;
    private final BluetoothClient client;

    public SPPDiscoveryListener(Object lock, String deviceIdentifier, BluetoothClient client) {
        this.lock = lock;
        this.deviceIdentifier = deviceIdentifier;
        this.client = client;
    }

    @Override
    public void deviceDiscovered(RemoteDevice btDevice, DeviceClass arg1) {
        String name = null;
        try {
            name = btDevice.getFriendlyName(false);
        } catch (IOException e) {
            name = btDevice.getBluetoothAddress();
        }

        if (name.contains(deviceIdentifier)) {
            client.targetDevice = btDevice;
        }
    }

    @Override
    public void inquiryCompleted(int arg0) {
        synchronized (lock) {
            lock.notify();
        }
    }

    @Override
    public void servicesDiscovered(int arg0, ServiceRecord[] services) {
        for (int i = 0; i < services.length; i++) {
            String url = services[i].getConnectionURL(ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false);
            if (url == null) {
                continue;
            }

            DataElement serviceName = services[i].getAttributeValue(0x0100);

            client.rfcommService = services[i];
        }
    }

    @Override
    public void serviceSearchCompleted(int arg0, int arg1) {
        synchronized (lock) {
            lock.notify();
        }
    }
}
