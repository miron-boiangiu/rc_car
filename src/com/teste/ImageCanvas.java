package com.teste;

import java.awt.*;

public class ImageCanvas extends Canvas {
    private volatile byte[][] image;
    private volatile byte[] image_array;
    public Color c = new Color(0, 0, 0);

    public ImageCanvas(byte[][] image, byte[] image_array) {
        this.image = image;
        this.image_array = image_array;

        setBackground(new Color(255, 255, 255));
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        for (int y = 0; y < 120; y++) {
            for (int x = 0; x < 160; x++) {
                int val = image[y][x] & 0xFF;
                g.setColor(new Color(val, val ,val));
                g.fillRect(4*x, 4*y, 4, 4);
            }
        }
    }
}
