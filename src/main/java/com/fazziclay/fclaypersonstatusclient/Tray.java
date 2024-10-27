package com.fazziclay.fclaypersonstatusclient;

import lombok.Setter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

public class Tray {
    private PopupMenu popup;
    private IconType currentIcon;
    private final TrayIcon trayIcon;
    @Setter
    private boolean configLoaded;
    MenuItem exitButton;
    CheckboxMenuItem postingButton;

    public Tray() {
        this.trayIcon = new TrayIcon(new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB), "FClaySPY", new PopupMenu());
    }

    public void start() {
        popup = new PopupMenu();
        popup.add(new MenuItem("Waiting..."));
        trayIcon.setPopupMenu(popup);
        trayIcon.setImageAutoSize(true);

        exitButton = new MenuItem("Exit");
        ActionListener exitListener = e -> {
            System.out.println("Exit tray icon clicked");
            int reply = JOptionPane.showConfirmDialog(null, "Are you sure you want to close?", "Close?", JOptionPane.YES_NO_OPTION);
            if (reply == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        };
        exitButton.addActionListener(exitListener);


        postingButton = new CheckboxMenuItem("Disable posting", Main.isDisablePosting());
        ItemListener po = e -> {
            System.out.println(e);
            Main.setDisablePosting(!Main.isDisablePosting());
            postingButton.setState(Main.isDisablePosting());
            tick();
        };
        postingButton.addItemListener(po);

        try {
            SystemTray tray = SystemTray.getSystemTray();
            tray.add(trayIcon);
        } catch (AWTException e) {
            System.err.println("TrayIcon could not be added.");
        }
    }

    public void tickIcon() {
        IconType iconType = IconType.NO_CONFIG;
        if (configLoaded) {
            if (Main.isDisablePosting()) {
                iconType = IconType.POSTING_DISABLED;
            } else if (Main.isPlaying()) {
                iconType = IconType.PLAYING;
            } else {
                iconType = IconType.SILENCE;
            }
        }
        setIconType(iconType);
    }


    public void setIconType(IconType iconType) {
        if (currentIcon != iconType) {
            trayIcon.setImage(iconType.getImage());
            currentIcon = iconType;
        }
    }

    public void tick() {
        tickIcon();
        updatePopup();
        trayIcon.setToolTip(Main.playbackDtoString());
    }

    private void updatePopup() {
        popup.removeAll();

        popup.add(Main.playbackDtoString());
        popup.add(postingButton);
        popup.add(exitButton);
        popup.add("");
    }
}
