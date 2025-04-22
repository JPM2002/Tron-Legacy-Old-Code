package tronlegacy.ui;

import tronlegacy.core.GamePanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class SpeedSelector extends JFrame implements ActionListener {
    private final JComboBox<String> comboBox;
    private final JComboBox<String> modeBox;


    public SpeedSelector() {
        setTitle("Select Speed");
        setSize(500, 200);
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        getContentPane().setBackground(Color.BLACK);
        setLayout(new BorderLayout());

        // Title
        JLabel title = new JLabel("CHOOSE YOUR SPEED", SwingConstants.CENTER);
        title.setForeground(new Color(0, 255, 255));
        title.setFont(new Font("Orbitron", Font.BOLD, 28));
        title.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));
        add(title, BorderLayout.NORTH);

        // Combo box
        String[] options = {"Slow", "Normal", "Fast", "Insane"};
        comboBox = new JComboBox<>(options);
        comboBox.setFont(new Font("Consolas", Font.BOLD, 22));
        comboBox.setBackground(Color.BLACK);
        comboBox.setForeground(Color.CYAN);
        comboBox.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        comboBox.setFocusable(false);
        comboBox.setBorder(BorderFactory.createLineBorder(Color.CYAN, 2));
        comboBox.addActionListener(this);


        String[] modes = {"Classic", "Power-Ups", "AI Bot"};
        modeBox = new JComboBox<>(modes);
        modeBox.setFont(new Font("Consolas", Font.BOLD, 22));
        modeBox.setBackground(Color.BLACK);
        modeBox.setForeground(Color.ORANGE);
        modeBox.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        modeBox.setFocusable(false);
        modeBox.setBorder(BorderFactory.createLineBorder(Color.ORANGE, 2));


        JPanel centerPanel = new JPanel();
        centerPanel.setBackground(Color.BLACK);
        centerPanel.add(comboBox);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 20))); // spacing
        centerPanel.add(modeBox);
        add(centerPanel, BorderLayout.CENTER);


        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String option = (String) comboBox.getSelectedItem();

        if (option != null) {
            if (option.contains("Slow")) GamePanel.DELAY = 100;
            else if (option.contains("Normal")) GamePanel.DELAY = 50;
            else if (option.contains("Fast")) GamePanel.DELAY = 25;
            else if (option.contains("Insane")) GamePanel.DELAY = 10;
        }

        // Set game mode
        String selectedMode = (String) modeBox.getSelectedItem();
        GamePanel.MODE = selectedMode;

        new GameWindow();
        dispose();
    }
}
