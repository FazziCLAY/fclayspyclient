package com.fazziclay.fclaypersonstatusclient;

import lombok.Getter;

import java.awt.*;
import java.awt.image.BufferedImage;

@Getter
public enum IconType {
    NO_CONFIG(new Color(83, 48, 0), false),
    SILENCE(new Color(42, 3, 75), false),
    POSTING_DISABLED(new Color(98, 72, 0), true),
    PLAYING(new Color(0, 128, 5), false);

    private final Color color;
    private final BufferedImage image;

    IconType(Color color, boolean square) {
        this.color = color;

        image = new BufferedImage(16, 16, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();

        graphics.setPaint(color);
        graphics.fillRect(0, 0, image.getWidth(), image.getHeight());
        if (square) {
            graphics.setPaint(Color.BLUE);
            graphics.fillRect(0, 0, image.getWidth()/2, image.getHeight()/2);
        }
        image.flush();
    }
}
