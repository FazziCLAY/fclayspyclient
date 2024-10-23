package com.fazziclay.fclaypersonstatusclient;

import java.awt.*;

public enum IconType {
    NO_CONFIG(new Color(83, 48, 0)),
    SILENCE(new Color(42, 3, 75)),
    POSTING_DISABLED(new Color(98, 72, 0)),
    PLAYING(new Color(0, 128, 5));

    private final Color color;

    IconType(Color color) {

        this.color = color;
    }

    public Color getColor() {
        return color;
    }
}
