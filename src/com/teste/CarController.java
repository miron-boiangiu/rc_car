package com.teste;

import com.teste.bluetoothclient.BluetoothClient;

import javax.swing.*;
import java.io.IOException;

import static java.awt.event.KeyEvent.*;

public class CarController {
    private final BluetoothClient bluetoothClient;
    public static char[] START_SEQUENCE = {'*', 'R', 'D', 'Y', '*'};
    private byte[][] image = new byte[120][160];
    private byte[] image_array = new byte[120*160];
    private final Keyboard keyboard = new Keyboard();

    public CarController() throws IOException {
        bluetoothClient = new BluetoothClient("Miron");

        SwingUtilities.invokeLater(() -> new ControllerGUI(image, image_array));

        Thread keyboardThread = new Thread(() -> {
            try {
                while (true) {
                    if (keyboard.isKeyPressed(VK_W)) {
                        bluetoothClient.sendByte((byte) 'W');
                        Thread.sleep(10);
                    } else if (keyboard.isKeyPressed(VK_X)) {
                        bluetoothClient.sendByte((byte) 'X');
                        Thread.sleep(10);
                    } else if (keyboard.isKeyPressed(VK_S)) {
                        bluetoothClient.sendByte((byte) 'S');
                        Thread.sleep(10);
                    }
                }
            }
            catch (IOException | InterruptedException e) {
                System.out.println(e);
            }
        });

        keyboardThread.start();
    }

    public void startController() throws IOException {
        int matched = 0;

        while(true) {
            if (bluetoothClient.isDataAvailable()) {
                byte readChar = bluetoothClient.getByte();

                if (readChar == START_SEQUENCE[matched]) {
                    matched++;

                    if (matched == START_SEQUENCE.length) {
                        System.out.println("Receiving frame.");
                        matched = 0;

                        for (int y = 0; y < 120; y++) {
                            for (int x = 0; x < 160; x++) {
                                while(!bluetoothClient.isDataAvailable());
                                byte b = bluetoothClient.getByte();
                                image[119-y][x] = b;
                                //image_array[y * 160 + x] = b;
                            }
                        }
                    }
                } else {
                    matched = 0;
                }
            }
        }
    }
}
