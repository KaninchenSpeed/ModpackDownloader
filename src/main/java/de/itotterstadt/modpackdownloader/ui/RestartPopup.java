package de.itotterstadt.modpackdownloader.ui;

import javax.swing.*;

public class RestartPopup {

    public static void showRestartPopup() {
        System.setProperty("java.awt.headless", "false");
        JOptionPane.showMessageDialog(null, "Please restart Minecraft", "Restart", JOptionPane.INFORMATION_MESSAGE);
        System.exit(0);
    }

}
