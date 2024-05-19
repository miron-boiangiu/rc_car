package com.teste;

import com.teste.bluetoothclient.BluetoothClient;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public class ControllerGUI extends JFrame{
    private Timer t;

    public ControllerGUI(byte[][] image, byte[] image_array) {
        this.setSize(640,480);

        final ImageCanvas c = new ImageCanvas(image, image_array);
        this.add(c);

        ActionListener taskPerformer = new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                System.out.println("Repainting.");
                c.repaint();
                //c.invalidate();
            }
        };

        t = new Timer(500 ,taskPerformer);
        t.setRepeats(true);
        t.start();

        this.setResizable(false);
        this.setVisible(true);//making the frame visible
    }
}
