import java.awt.*;
import java.awt.event.*;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;

public class GamePanel extends JPanel implements ActionListener {

    long elapsedTime = 0;
    int scoreMultiplier = 80;
    static final int SCREEN_WIDTH = 1280;
    static final int SCREEN_HEIGHT = 800;
    static final int UNIT_SIZE = 10;
    static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT) / (UNIT_SIZE * UNIT_SIZE);
    static int DELAY = 25;
    final int[] x_P1 = new int[GAME_UNITS];
    final int[] y_P1 = new int[GAME_UNITS];
    final int[] x_P2 = new int[GAME_UNITS];
    final int[] y_P2 = new int[GAME_UNITS];
    int LightCycle_1 = 2;
    int LightCycle_2 = 2;
    char direction_P1 = 'R';
    char direction_P2 = 'L';
    private boolean player1Alive = true;
    private boolean player2Alive = true;
    boolean running = false;
    Timer timer;
    Random random;

    GamePanel() {
        random = new Random();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.black);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        startGame();
    }
    private void initGame() {
        // Set initial position and direction for player 1
        x_P1[0] = UNIT_SIZE * 3;
        y_P1[0] = UNIT_SIZE * 3;

        // Set initial position and direction for player 2
        x_P2[0] = UNIT_SIZE * (SCREEN_WIDTH / UNIT_SIZE - 3);
        y_P2[0] = UNIT_SIZE * 3;

        try {
            // Load the sound file
            File soundFile = new File("C:\\Users\\Javie\\IdeaProjects\\Tron_Legacy\\src\\Music\\Tron_Game.wav");
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(soundFile);

            // Get a clip to play the sound
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);

            // Start playing the sound
            clip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    public void startGame() {
        running = true;
        elapsedTime = 0;
        timer = new Timer(DELAY, this);
        timer.start();
        initGame();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }
//TODO: do not erase!!!
    public void draw(Graphics g) {
        if (running) {
            g.setColor(Color.CYAN);
            for (int i = 0; i <= SCREEN_WIDTH; i += UNIT_SIZE) {
                g.drawLine(i, 0, i, SCREEN_HEIGHT);
            }
            for (int i = 0; i <= SCREEN_HEIGHT; i += UNIT_SIZE) {
                g.drawLine(0, i, SCREEN_WIDTH, i);
            }

            for (int i = 0; i < LightCycle_1; i++) {
                if (i == 0) {
                    g.setColor(new Color(16, 220, 238));
                    g.fillRect(x_P1[i], y_P1[i], UNIT_SIZE, UNIT_SIZE);
                    i = 1;
                    g.fillRect(x_P1[i], y_P1[i], UNIT_SIZE, UNIT_SIZE);
                }else {
                    g.setColor(new Color(13, 147, 178));
                    g.fillRect(x_P1[i], y_P1[i], UNIT_SIZE, UNIT_SIZE);
                }
            }
            for (int i = 0; i < LightCycle_2; i++) {
                if (i == 0) {
                    g.setColor(new Color(245, 98, 10));
                    g.fillRect(x_P2[i], y_P2[i], UNIT_SIZE, UNIT_SIZE);
                    i = 1;
                    g.fillRect(x_P2[i], y_P2[i], UNIT_SIZE, UNIT_SIZE);
                } else {
                    g.setColor(new Color(199, 118, 16));
                    g.fillRect(x_P2[i], y_P2[i], UNIT_SIZE, UNIT_SIZE);
                }
            }
            g.setColor(Color.blue);
            g.setFont(new Font("Ink Free", Font.BOLD, 50));
            FontMetrics metrics = getFontMetrics(g.getFont());
            String scoreText = "Score: " + (elapsedTime / scoreMultiplier);
            g.drawString(scoreText, (SCREEN_WIDTH - metrics.stringWidth(scoreText)) / 2, g.getFont().getSize());
        } else {
            gameOver(g);
        }
    }
    public void Trail() {
        LightCycle_1++;
        LightCycle_2++;
    }
    public void move_P1(){
        for (int i = LightCycle_1 - 1; i > 0; i--) {
            x_P1[i] = x_P1[i - 1];
            y_P1[i] = y_P1[i - 1];
        }

        if (direction_P1 == 'U') {
            y_P1[0] = y_P1[0] - UNIT_SIZE;
        } else if (direction_P1 == 'D') {
            y_P1[0] = y_P1[0] + UNIT_SIZE;
        } else if (direction_P1 == 'L') {
            x_P1[0] = x_P1[0] - UNIT_SIZE;
        } else if (direction_P1 == 'R') {
            x_P1[0] = x_P1[0] + UNIT_SIZE;
        }
        elapsedTime += DELAY;
    }
    public void move_P2() {
        for (int i = LightCycle_2 - 1; i > 0; i--) {
            x_P2[i] = x_P2[i - 1];
            y_P2[i] = y_P2[i - 1];
        }

        if (direction_P2 == 'U') {
            y_P2[0] = y_P2[0] - UNIT_SIZE;
        } else if (direction_P2 == 'D') {
            y_P2[0] = y_P2[0] + UNIT_SIZE;
        } else if (direction_P2 == 'L') {
            x_P2[0] = x_P2[0] - UNIT_SIZE;
        } else if (direction_P2 == 'R') {
            x_P2[0] = x_P2[0] + UNIT_SIZE;
        }
        elapsedTime += DELAY;
    }

    public void gameOver(Graphics g) {
        // Determine which player won the game
        String winner = "Nobody";
        if (player2Alive == false ){
            winner = "Player 1";
        } else if (player1Alive == false ){
            winner = "Player 2";
        }

        // Display the winner on the screen
        g.setColor(Color.BLUE);
        g.setFont(new Font("Ink Free", Font.BOLD, 40));
        String msg = winner + " wins!";
        FontMetrics metrics_winner = getFontMetrics(g.getFont());
        g.drawString(msg, (SCREEN_WIDTH - metrics_winner.stringWidth(msg)) / 2, SCREEN_HEIGHT / 2+ 235);

        // Stop the game loop
        running = false;
        // Draw game over message
        g.setColor(Color.blue);
        g.setFont(new Font("Ink Free", Font.BOLD, 75));
        FontMetrics metrics = getFontMetrics(g.getFont());
        String gameOverText = "GAME OVER";
        g.drawString(gameOverText, (SCREEN_WIDTH - metrics.stringWidth(gameOverText)) / 2, SCREEN_HEIGHT / 2);
        //Score
        g.setColor(Color.white);
        g.setFont( new Font("Ink Free",Font.BOLD, 50));
        FontMetrics metrics1 = getFontMetrics(g.getFont());
        g.drawString("Score: " + elapsedTime / scoreMultiplier, (SCREEN_WIDTH - metrics1.stringWidth("Score: "+ elapsedTime / scoreMultiplier ))/2,g.getFont().getSize());
        // Draw restart button
        g.setColor(Color.white);
        int restartButtonX = (SCREEN_WIDTH - 200) / 2;
        int restartButtonY = SCREEN_HEIGHT / 2 + 100;
        g.fillRect(restartButtonX, restartButtonY, 200, 50);
        g.setColor(Color.black);
        g.setFont(new Font("Ink Free", Font.BOLD, 25));
        metrics = getFontMetrics(g.getFont());
        String restartText = "RESTART ?";
        g.drawString(restartText, restartButtonX + (200 - metrics.stringWidth(restartText)) / 2, restartButtonY + 35);

// Draw quit button
        g.setColor(Color.white);
        int quitButtonX = (SCREEN_WIDTH - 660) / 2;
        int quitButtonY = SCREEN_HEIGHT / 2 + 100;
        g.fillRect(quitButtonX, quitButtonY, 200, 50);
        g.setColor(Color.black);
        g.setFont(new Font("Ink Free", Font.BOLD, 25));
        metrics = getFontMetrics(g.getFont());
        String quitText = "Quit";
        g.drawString(quitText, quitButtonX + (200 - metrics.stringWidth(quitText)) / 2, quitButtonY + 35);

// Draw speed selector button
        g.setColor(Color.white);
        int speedButtonX = (SCREEN_WIDTH + 250) / 2;
        int speedButtonY = SCREEN_HEIGHT / 2 + 100;
        g.fillRect(speedButtonX, speedButtonY, 200, 50);
        g.setColor(Color.black);
        g.setFont(new Font("Ink Free", Font.BOLD, 25));
        metrics = getFontMetrics(g.getFont());
        String speedText = "Speed";
        g.drawString(speedText, speedButtonX + (200 - metrics.stringWidth(speedText)) / 2, speedButtonY + 35);

// Add mouse listener to detect button click
        this.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int mouseX = e.getX();
                int mouseY = e.getY();

                // Check if mouse click is within restart button bounds
                if (mouseX >= restartButtonX && mouseX <= restartButtonX + 200 &&
                        mouseY >= restartButtonY && mouseY <= restartButtonY + 50) {
                    restartGame();

                }
                // Check if mouse click is within quit button bounds
                else if (mouseX >= quitButtonX && mouseX <= quitButtonX + 200 &&
                        mouseY >= quitButtonY && mouseY <= quitButtonY + 50) {
                    System.exit(0);
                }
                // Check if mouse click is within speed selector button bounds
                else if (mouseX >= speedButtonX && mouseX <= speedButtonX + 200 &&
                        mouseY >= speedButtonY && mouseY <= speedButtonY + 50) {
                    new Speed();
                }
            }
        });
    }
    public void restartGame() {
        new GameFrame();
    }

    public void checkCollisions(int[] x_P1, int[] y_P1, int[] x_P2, int[] y_P2) {
        // checks if head of player 1 collides with body of player 1
        for (int i = 1; i < LightCycle_1; i++) {
            if ((x_P1[0] == x_P1[i]) && (y_P1[0] == y_P1[i])) {
                running = false;
                player1Alive = false;
                break;
            }
        }
        // checks if head of player 2 collides with body of player 2
        for (int i = 1; i < LightCycle_2; i++) {
            if ((x_P2[0] == x_P2[i]) && (y_P2[0] == y_P2[i])) {
                running = false;
                player2Alive = false;
                break;
            }
        }
        // checks if head of player 1 collides with body of player 2
        for (int i = 0; i < LightCycle_2; i++) {
            if ((x_P1[0] == x_P2[i]) && (y_P1[0] == y_P2[i])) {
                running = false;
                player1Alive = false;
                break;
            }
        }
        // checks if head of player 2 collides with body of player 1
        for (int i = 0; i < LightCycle_1; i++) {
            if ((x_P2[0] == x_P1[i]) && (y_P2[0] == y_P1[i])) {
                running = false;
                player2Alive = false;
                break;
            }
        }
        // checks if head of player 1 touch left border
        if (x_P1[0] < 0) {
            player1Alive = false;
            running = false;
        }
        // checks if head of player 1 touch right border
        if (x_P1[0] > SCREEN_WIDTH) {
            player1Alive = false;
            running = false;
        }
        // checks if head of player 1 touch top border
        if (y_P1[0] < 0) {
            player1Alive = false;
            running = false;
        }
        // checks if head of player 1 touch bottom border
        if (y_P1[0] > SCREEN_HEIGHT) {
            player1Alive = false;
            running = false;
        }
        // checks if head of player 2 touches left border
        if (x_P2[0] < 0) {
            player2Alive = false;
            running = false;
        }
        // checks if head of player 2 touches right border
        if (x_P2[0] > SCREEN_WIDTH) {
            player2Alive = false;
            running = false;
        }
        // checks if head of player 2 touches top border
        if (y_P2[0] < 0) {
            player2Alive = false;
            running = false;
        }
        // checks if head of player 2 touches bottom border
        if (y_P2[0] > SCREEN_HEIGHT) {
            player2Alive = false;
            running = false;
        }

        if (!running) {
            timer.stop();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        elapsedTime += DELAY;
        if (running) {
            move_P1();
            move_P2();
            Trail();
            checkCollisions(x_P1, y_P1, x_P2, y_P2);

        }
        repaint();
    }
    public void mouseClicked(MouseEvent e) {
        int mouseX = e.getX();
        int mouseY = e.getY();
        if (mouseX >= 100 && mouseX <= 300 && mouseY >= 250 && mouseY <= 300) {
            restartGame();
        }
    }

    class MyKeyAdapter extends KeyAdapter {
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT -> {
                    if (direction_P2 != 'R') {
                        direction_P2 = 'L';
                    }
                }
                case KeyEvent.VK_RIGHT -> {
                    if (direction_P2 != 'L') {
                        direction_P2 = 'R';
                    }
                }
                case KeyEvent.VK_UP -> {
                    if (direction_P2 != 'D') {
                        direction_P2 = 'U';
                    }
                }
                case KeyEvent.VK_DOWN -> {
                    if (direction_P2 != 'U') {
                        direction_P2 = 'D';
                    }
                }
                case KeyEvent.VK_A -> {
                    if (direction_P1 != 'R') {
                        direction_P1 = 'L';
                    }
                }
                case KeyEvent.VK_D -> {
                    if (direction_P1 != 'L') {
                        direction_P1 = 'R';
                    }
                }
                case KeyEvent.VK_W -> {
                    if (direction_P1 != 'D') {
                        direction_P1 = 'U';
                    }
                }
                case KeyEvent.VK_S -> {
                    if (direction_P1 != 'U') {
                        direction_P1 = 'D';
                    }
                }
            }
        }
    }
}
