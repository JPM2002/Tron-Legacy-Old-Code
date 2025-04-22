// tronlegacy/core/GamePanel.java
// (PAUSE, RESTART & POWER-UPS MODALITY ENHANCED)
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
    public static String MODE = "Classic";

    private static final int SCREEN_WIDTH = 1280;
    private static final int SCREEN_HEIGHT = 800;
    private static final int UNIT_SIZE = 10;
    private static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT) / (UNIT_SIZE * UNIT_SIZE);
    private static final int POWERUP_SIZE = UNIT_SIZE * 2;

    enum PowerUpType { SHIELD, SPEED, REVERSE }

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

    private boolean p1Shield = false, p2Shield = false;
    private long p1ShieldEnd = 0, p2ShieldEnd = 0;
    private int powerUpX = -1, powerUpY = -1;
    private PowerUpType activePowerUp = null;
    private long powerUpSpawnTime = 0;

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
        p1Shield = false;
        p2Shield = false;
        activePowerUp = null;
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
        playSound("assets/Music/Tron_Game.wav");
    }

    private void drawGameOver(Graphics g) {
        String winner = !player2Alive ? "Player 1" : (!player1Alive ? "Player 2" : "Nobody");

        g.setColor(Color.CYAN);
        g.setFont(new Font("Consolas", Font.BOLD, 70));
        g.drawString("GAME OVER", (SCREEN_WIDTH - g.getFontMetrics().stringWidth("GAME OVER")) / 2, SCREEN_HEIGHT / 2 - 60);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Consolas", Font.BOLD, 40));
        String winText = winner + " wins!";
        g.drawString(winText, (SCREEN_WIDTH - g.getFontMetrics().stringWidth(winText)) / 2, SCREEN_HEIGHT / 2);

        g.setColor(Color.LIGHT_GRAY);
        g.setFont(new Font("Consolas", Font.PLAIN, 26));
        String restartInfo = "Press 'R' to restart";
        g.drawString(restartInfo, (SCREEN_WIDTH - g.getFontMetrics().stringWidth(restartInfo)) / 2, SCREEN_HEIGHT / 2 + 60);
    }


    private void playSound(String filePath) {
        try {
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
            g.setColor(new Color(40, 40, 40));
            for (int i = 0; i <= SCREEN_WIDTH; i += UNIT_SIZE) g.drawLine(i, 0, i, SCREEN_HEIGHT);
            for (int i = 0; i <= SCREEN_HEIGHT; i += UNIT_SIZE) g.drawLine(0, i, SCREEN_WIDTH, i);

            for (int i = 0; i < LightCycle_1; i++) {
                g.setColor(i == 0 ? new Color(0, 255, 255) : new Color(0, 180, 220));
                g.fillRect(x_P1[i], y_P1[i], UNIT_SIZE, UNIT_SIZE);
            }
            for (int i = 0; i < LightCycle_2; i++) {
                g.setColor(i == 0 ? new Color(255, 100, 0) : new Color(200, 80, 20));
                g.fillRect(x_P2[i], y_P2[i], UNIT_SIZE, UNIT_SIZE);
            }

            g.setColor(Color.CYAN);
            g.setFont(new Font("Consolas", Font.BOLD, 32));
            g.drawString("Score: " + (elapsedTime / scoreMultiplier), 30, 50);
            g.setColor(Color.LIGHT_GRAY);
            g.setFont(new Font("Consolas", Font.PLAIN, 18));
            g.drawString("Mode: " + MODE, 30, 75);

            if (MODE.equals("Power-Ups") && activePowerUp != null) {
                switch (activePowerUp) {
                    case SHIELD -> g.setColor(Color.GREEN);
                    case SPEED -> g.setColor(Color.YELLOW);
                    case REVERSE -> g.setColor(Color.MAGENTA);
                }
                g.fillOval(powerUpX, powerUpY, POWERUP_SIZE, POWERUP_SIZE);
            }

            if (paused) {
                g.setColor(Color.WHITE);
                g.setFont(new Font("Consolas", Font.BOLD, 60));
                g.drawString("⏸ PAUSED", (SCREEN_WIDTH - g.getFontMetrics().stringWidth("⏸ PAUSED")) / 2, SCREEN_HEIGHT / 2);
            }
        } else {
            drawGameOver(g);
        }
    }

    private void spawnPowerUp() {
        if (System.currentTimeMillis() - powerUpSpawnTime < 8000 || activePowerUp != null) return;
        powerUpX = (int) (Math.random() * (SCREEN_WIDTH / UNIT_SIZE)) * UNIT_SIZE;
        powerUpY = (int) (Math.random() * (SCREEN_HEIGHT / UNIT_SIZE)) * UNIT_SIZE;
        activePowerUp = PowerUpType.values()[(int) (Math.random() * 3)];
        powerUpSpawnTime = System.currentTimeMillis();
    }

    private void applyPowerUpToPlayer(int player) {
        switch (activePowerUp) {
            case SHIELD -> {
                if (player == 1) {
                    p1Shield = true;
                    p1ShieldEnd = System.currentTimeMillis() + 5000;
                } else {
                    p2Shield = true;
                    p2ShieldEnd = System.currentTimeMillis() + 5000;
                }
            }
            case SPEED -> DELAY = Math.max(10, DELAY - 10);
            case REVERSE -> {
                if (player == 1) direction_P2 = reverseDirection(direction_P2);
                else direction_P1 = reverseDirection(direction_P1);
            }
        }
        activePowerUp = null;
    }

    private char reverseDirection(char dir) {
        return switch (dir) {
            case 'U' -> 'D';
            case 'D' -> 'U';
            case 'L' -> 'R';
            case 'R' -> 'L';
            default -> dir;
        };
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
        if (!p1Shield) {
            for (int i = 1; i < LightCycle_1; i++)
                if (x_P1[0] == x_P1[i] && y_P1[0] == y_P1[i]) player1Alive = running = false;

            for (int i = 0; i < LightCycle_2; i++)
                if (x_P1[0] == x_P2[i] && y_P1[0] == y_P2[i]) player1Alive = running = false;

            if (x_P1[0] < 0 || x_P1[0] >= SCREEN_WIDTH || y_P1[0] < 0 || y_P1[0] >= SCREEN_HEIGHT)
                player1Alive = running = false;
        }

        if (!p2Shield) {
            for (int i = 1; i < LightCycle_2; i++)
                if (x_P2[0] == x_P2[i] && y_P2[0] == y_P2[i]) player2Alive = running = false;

            for (int i = 0; i < LightCycle_1; i++)
                if (x_P2[0] == x_P1[i] && y_P2[0] == y_P1[i]) player2Alive = running = false;

            if (x_P2[0] < 0 || x_P2[0] >= SCREEN_WIDTH || y_P2[0] < 0 || y_P2[0] >= SCREEN_HEIGHT)
                player2Alive = running = false;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running && !paused) {
            elapsedTime += DELAY;
            movePlayer(x_P1, y_P1, direction_P1, LightCycle_1);
            movePlayer(x_P2, y_P2, direction_P2, LightCycle_2);
            LightCycle_1++;
            LightCycle_2++;

            if (MODE.equals("Power-Ups")) {
                spawnPowerUp();
                Rectangle powerRect = new Rectangle(powerUpX, powerUpY, POWERUP_SIZE, POWERUP_SIZE);
                Rectangle p1Rect = new Rectangle(x_P1[0], y_P1[0], UNIT_SIZE, UNIT_SIZE);
                Rectangle p2Rect = new Rectangle(x_P2[0], y_P2[0], UNIT_SIZE, UNIT_SIZE);

                if (activePowerUp != null) {
                    if (p1Rect.intersects(powerRect)) applyPowerUpToPlayer(1);
                    else if (p2Rect.intersects(powerRect)) applyPowerUpToPlayer(2);
                }

                if (p1Shield && System.currentTimeMillis() > p1ShieldEnd) p1Shield = false;
                if (p2Shield && System.currentTimeMillis() > p2ShieldEnd) p2Shield = false;
            }

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