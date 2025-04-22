import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class mainMenu extends JFrame implements ActionListener {
    private JButton button1;
    private JButton button2;
    private JButton button3;
    static final int SCREEN_WIDTH = 400;
    static final int SCREEN_HEIGHT = 100;
    mainMenu(){
        this.setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
        this.setTitle("Tron: Menu");
        this.setResizable(false);
        this.setVisible(true);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(new GridBagLayout());

        // Initialize the play button
        button1.addActionListener(this);
        this.add(button1);
        // Initialize the leaderboard button
        button2.addActionListener(this);
        this.add(button2);
        // Initialize the exit button
        button3.addActionListener(this);
        this.add(button3);

        this.setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == button1) {
            // Start the game when the play button is clicked
            this.dispose(); // Close the main menu window
            new Speed();
        } else if (e.getSource() == button3) {
            // Exit the game when the exit button is clicked
            System.exit(0);
        }
    }
}
