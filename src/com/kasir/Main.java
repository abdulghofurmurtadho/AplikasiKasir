package com.kasir;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * Class utama untuk menjalankan aplikasi kasir.
 */
public class Main {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | UnsupportedLookAndFeelException e) {
            // Jika gagal, pakai tampilan default Swing
        }
        SwingUtilities.invokeLater(() -> new FrameKasir().setVisible(true));
    }
}
