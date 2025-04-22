// tronlegacy/core/GamePanel.java
// (PAUSE & RESTART ENHANCED)
package tronlegacy.core;

import tronlegacy.ui.GameWindow;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;

public class GamePanel extends JPanel implements ActionListener {
    public static int DELAY = 25;
    private static final int SCREEN_WIDTH = 1280;
    private static final int SCREEN_HEIGHT = 800;
    private static final int UNIT_SIZE = 10;
    private static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT) / (UNIT_SIZE * UNIT_SIZE);

    private final int[] x_P1 = new int[GAME_UNITS];
    private final int[] y_P1 = new int[GAME_UNITS];
    private final int[] x_P2 = new int[GAME_UNITS];
    private final int[] y_P2 = new int[GAME_UNITS];

    private int LightCycle_1 = 2;
    private int LightCycle_2 = 2;
    private char direction_P1 = 'R';
    private char direction_P2 = 'L';
    private boolean player1Alive = true;
    private boolean player2Alive = true;
    private boolean running = false;
    private boolean paused = false;

    private Timer timer;
    private long elapsedTime = 0;
    private final int scoreMultiplier = 80;

    public GamePanel() {
        setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(new MyKeyAdapter());
        initGame();
        startGame();
    }

    private void initGame() {
        LightCycle_1 = 2;
        LightCycle_2 = 2;
        direction_P1 = 'R';
        direction_P2 = 'L';
        player1Alive = true;
        player2Alive = true;
        elapsedTime = 0;
        for (int i = 0; i < GAME_UNITS; i++) {
            x_P1[i] = 0;
            y_P1[i] = 0;
            x_P2[i] = 0;
            y_P2[i] = 0;
        }
        x_P1[0] = UNIT_SIZE * 3;
        y_P1[0] = UNIT_SIZE * 3;
        x_P2[0] = UNIT_SIZE * (SCREEN_WIDTH / UNIT_SIZE - 3);
        y_P2[0] = UNIT_SIZE * 3;
        playSound("assets/Music/Tron_Game.wav"); // This stays the same

    }

    private void playSound(String filePath) {
        try {
            // Load sound as resource from classpath
            File file = new File("src/" + filePath);
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file);
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.err.println("Error playing sound: " + e.getMessage());
        }
    }


    private void startGame() {
        running = true;
        timer = new Timer(DELAY, this);
        timer.start();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    private void draw(Graphics g) {
        if (running) {
            // Draw grid
            g.setColor(new Color(40, 40, 40)); // slightly brighter than DARK_GRAY
            for (int i = 0; i <= SCREEN_WIDTH; i += UNIT_SIZE) {
                g.drawLine(i, 0, i, SCREEN_HEIGHT);
            }
            for (int i = 0; i <= SCREEN_HEIGHT; i += UNIT_SIZE) {
                g.drawLine(0, i, SCREEN_WIDTH, i);
            }

            // Player 1 trail
            for (int i = 0; i < LightCycle_1; i++) {
                g.setColor(i == 0 ? new Color(0, 255, 255) : new Color(0, 180, 220));
                g.fillRect(x_P1[i], y_P1[i], UNIT_SIZE, UNIT_SIZE);
            }

            // Player 2 trail
            for (int i = 0; i < LightCycle_2; i++) {
                g.setColor(i == 0 ? new Color(255, 100, 0) : new Color(200, 80, 20));
                g.fillRect(x_P2[i], y_P2[i], UNIT_SIZE, UNIT_SIZE);
            }

            // Score HUD
            g.setColor(Color.CYAN);
            g.setFont(new Font("Consolas", Font.BOLD, 32));
            String scoreText = "Score: " + (elapsedTime / scoreMultiplier);
            g.drawString(scoreText, 30, 50); // Top-left instead of center

            // Pause overlay
            if (paused) {
                g.setColor(Color.WHITE);
                g.setFont(new Font("Consolas", Font.BOLD, 60));
                String pauseText = "⏸ PAUSED";
                g.drawString(pauseText, (SCREEN_WIDTH - g.getFontMetrics().stringWidth(pauseText)) / 2, SCREEN_HEIGHT / 2);
            }
        } else {
            drawGameOver(g);
        }
    }

    private void drawGameOver(Graphics g) {
        String winner = !player2Alive ? "Player 1" : (!player1Alive ? "Player 2" : "Nobody");

        // Title
        g.setColor(Color.CYAN);
        g.setFont(new Font("Consolas", Font.BOLD, 70));
        g.drawString("GAME OVER", (SCREEN_WIDTH - g.getFontMetrics().stringWidth("GAME OVER")) / 2, SCREEN_HEIGHT / 2 - 60);

        // Winner
        g.setColor(Color.WHITE);
        g.setFont(new Font("Consolas", Font.BOLD, 40));
        String winText = winner + " wins!";
        g.drawString(winText, (SCREEN_WIDTH - g.getFontMetrics().stringWidth(winText)) / 2, SCREEN_HEIGHT / 2);

        // Restart info
        g.setColor(Color.LIGHT_GRAY);
        g.setFont(new Font("Consolas", Font.PLAIN, 26));
        String restartInfo = "Press 'R' to restart";
        g.drawString(restartInfo, (SCREEN_WIDTH - g.getFontMetrics().stringWidth(restartInfo)) / 2, SCREEN_HEIGHT / 2 + 60);
    }


    private void movePlayer(int[] x, int[] y, char direction, int length) {
        for (int i = length - 1; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }
        switch (direction) {
            case 'U' -> y[0] -= UNIT_SIZE;
            case 'D' -> y[0] += UNIT_SIZE;
            case 'L' -> x[0] -= UNIT_SIZE;
            case 'R' -> x[0] += UNIT_SIZE;
        }
    }

    private void checkCollisions() {
        for (int i = 1; i < LightCycle_1; i++)
            if (x_P1[0] == x_P1[i] && y_P1[0] == y_P1[i]) player1Alive = running = false;

        for (int i = 1; i < LightCycle_2; i++)
            if (x_P2[0] == x_P2[i] && y_P2[0] == y_P2[i]) player2Alive = running = false;

        for (int i = 0; i < LightCycle_2; i++)
            if (x_P1[0] == x_P2[i] && y_P1[0] == y_P2[i]) player1Alive = running = false;

        for (int i = 0; i < LightCycle_1; i++)
            if (x_P2[0] == x_P1[i] && y_P2[0] == y_P1[i]) player2Alive = running = false;

        if (x_P1[0] < 0 || x_P1[0] >= SCREEN_WIDTH || y_P1[0] < 0 || y_P1[0] >= SCREEN_HEIGHT) player1Alive = running = false;
        if (x_P2[0] < 0 || x_P2[0] >= SCREEN_WIDTH || y_P2[0] < 0 || y_P2[0] >= SCREEN_HEIGHT) player2Alive = running = false;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running && !paused) {
            elapsedTime += DELAY;
            movePlayer(x_P1, y_P1, direction_P1, LightCycle_1);
            movePlayer(x_P2, y_P2, direction_P2, LightCycle_2);
            LightCycle_1++;
            LightCycle_2++;
            checkCollisions();
        }
        repaint();
    }

    private class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_A -> { if (direction_P1 != 'R') direction_P1 = 'L'; }
                case KeyEvent.VK_D -> { if (direction_P1 != 'L') direction_P1 = 'R'; }
                case KeyEvent.VK_W -> { if (direction_P1 != 'D') direction_P1 = 'U'; }
                case KeyEvent.VK_S -> { if (direction_P1 != 'U') direction_P1 = 'D'; }
                case KeyEvent.VK_LEFT -> { if (direction_P2 != 'R') direction_P2 = 'L'; }
                case KeyEvent.VK_RIGHT -> { if (direction_P2 != 'L') direction_P2 = 'R'; }
                case KeyEvent.VK_UP -> { if (direction_P2 != 'D') direction_P2 = 'U'; }
                case KeyEvent.VK_DOWN -> { if (direction_P2 != 'U') direction_P2 = 'D'; }
                case KeyEvent.VK_P -> paused = !paused;
                case KeyEvent.VK_R -> {
                    if (!running) {
                        initGame();
                        running = true;
                        timer.restart();
                    }
                }
            }
        }
    }
}
