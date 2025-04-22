package tronlegacy.ui;

import tronlegacy.core.GamePanel;

import javax.swing.*;

public class GameWindow extends JFrame {
    public GameWindow() {
        add(new GamePanel());
        setTitle("Tron");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
}
