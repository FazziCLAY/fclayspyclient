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

    public Tray() {
        this.trayIcon = new TrayIcon(new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB), "FClaySPY", new PopupMenu());
    }

    public void start() {
        popup = new PopupMenu();
        popup.add(new MenuItem("Waiting..."));
        trayIcon.setPopupMenu(popup);

        trayIcon.setImageAutoSize(true);

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
            BufferedImage img = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
            Graphics2D graphics = img.createGraphics();

            graphics.setPaint(iconType.getColor());
            graphics.fillRect(0, 0, img.getWidth(), img.getHeight());
            if (iconType == IconType.POSTING_DISABLED) {
                graphics.setPaint(Color.BLUE);
                graphics.fillRect(0, 0, img.getWidth()/2, img.getHeight()/2);
            }
            if (iconType == IconType.PLAYING) {
                graphics.setPaint(new Color((int) System.currentTimeMillis()));
                graphics.fillRect(0, img.getHeight()-1, img.getWidth(), img.getHeight());
            }
            img.flush();


            trayIcon.setImage(img);
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

        MenuItem exit = new MenuItem("Exit");
        ActionListener exitListener = e -> {
            System.out.println("Exit tray icon clicked");
            int reply = JOptionPane.showConfirmDialog(null, "Are you sure you want to close?", "Close?", JOptionPane.YES_NO_OPTION);
            if (reply == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        };
        exit.addActionListener(exitListener);

        CheckboxMenuItem posting = new CheckboxMenuItem("Disable posting", Main.isDisablePosting());
        ItemListener po = e -> {
            System.out.println(e);
            Main.setDisablePosting(!Main.isDisablePosting());
            posting.setState(Main.isDisablePosting());
            tick();
        };
        posting.addItemListener(po);

        popup.add(Main.playbackDtoString());
        popup.add(posting);
        popup.add(exit);
        popup.add("");
    }
}
