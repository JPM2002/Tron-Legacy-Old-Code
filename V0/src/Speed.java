import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Speed extends JFrame implements ActionListener {
    private JComboBox<String> comboBox;
    private JPanel panel;
    static final int SCREEN_WIDTH = 400;
    static final int SCREEN_HEIGHT = 100;

    Speed(){
        this.setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
        this.setTitle("Tron: Speed");
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Initialize the JComboBox
        String[] options = {"Slow", "Normal", "Fast","Insane"};
        comboBox = new JComboBox<>(options);
        comboBox.addActionListener(this);
        this.add(comboBox);

        this.setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == comboBox) {
            // Get the selected option from the JComboBox
            String selectedOption = (String) comboBox.getSelectedItem();

            assert selectedOption != null;
            switch (selectedOption) {
                case "Slow" -> {
                    GamePanel.DELAY = 100;
                    new GameFrame();
                }
                case "Normal" -> {
                    GamePanel.DELAY = 50;
                    new GameFrame();
                }
                case "Fast" -> {
                    GamePanel.DELAY = 25;
                    new GameFrame();
                }
                case "Insane" -> {
                    GamePanel.DELAY = 10;
                    new GameFrame();
                }
            }
        }
    }
}
