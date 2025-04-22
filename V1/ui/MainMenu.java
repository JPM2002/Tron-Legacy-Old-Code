package tronlegacy.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MainMenu extends JFrame implements ActionListener {
    private final JButton playButton = new JButton("Play");
    private final JButton leaderboardButton = new JButton("Leaderboard");
    private final JButton exitButton = new JButton("Exit");

    public MainMenu() {
        setTitle("Tron Legacy");
        setSize(500, 300);
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        getContentPane().setBackground(Color.BLACK);
        setLayout(new BorderLayout());

        // Title
        JLabel title = new JLabel("TRON LEGACY", SwingConstants.CENTER);
        title.setForeground(new Color(0, 255, 255));
        title.setFont(new Font("Orbitron", Font.BOLD, 36));
        title.setBorder(BorderFactory.createEmptyBorder(30, 10, 20, 10));
        add(title, BorderLayout.NORTH);

        // Button Panel
        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        buttonPanel.setBackground(Color.BLACK);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 80, 40, 80));

        styleButton(playButton, new Color(0, 255, 255));
        styleButton(leaderboardButton, new Color(255, 165, 0));
        styleButton(exitButton, new Color(255, 50, 50));

        buttonPanel.add(playButton);
        buttonPanel.add(leaderboardButton);
        buttonPanel.add(exitButton);

        add(buttonPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    private void styleButton(JButton button, Color glow) {
        button.setFocusPainted(false);
        button.setFont(new Font("Consolas", Font.BOLD, 20));
        button.setBackground(Color.BLACK);
        button.setForeground(glow);
        button.setBorder(BorderFactory.createLineBorder(glow, 2));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setContentAreaFilled(false);
        button.setOpaque(true);
        button.addActionListener(this);

        // Hover effect
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(glow);
                button.setForeground(Color.BLACK);
            }

            public void mouseExited(MouseEvent e) {
                button.setBackground(Color.BLACK);
                button.setForeground(glow);
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source == playButton) {
            dispose();
            new SpeedSelector();
        } else if (source == exitButton) {
            System.exit(0);
        } else if (source == leaderboardButton) {
            JOptionPane.showMessageDialog(this, "Leaderboard coming soon!");
        }
    }
}
