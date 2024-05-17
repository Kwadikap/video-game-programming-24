import javax.swing.*;
import java.awt.event.*;

//The main issue with this code is that it tries to run the game directly on the event dispatch thread (EDT) when the "Start" button is clicked.
//This leads to UI responsiveness issues because the game loop is likely to be intensive and can block the EDT.
//When the "Start" button is clicked, gameManager.run() is called directly within the actionPerformed method, which is executed on the EDT
// Making the game loop block the EDT when gameManager.run() is

public class MenuTest extends JFrame implements ActionListener {
    private GameManager gameManager; // GameManager instance

    public MenuTest() {
        // Set up the frame
        setTitle("Game Menu");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create a "Start" button
        JButton startButton = new JButton("Start");
        startButton.addActionListener(this); // Add action listener to the button

        // Add the "Start" button to the frame
        JPanel panel = new JPanel();
        panel.add(startButton);
        add(panel);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Handle button click events
        if (e.getActionCommand().equals("Start")) {
            // Create a new GameManager instance
            gameManager = new GameManager();

            // Start the game in a separate thread
            Thread gameThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    gameManager.run();
                }
            });
            gameThread.start();

            // Hide the menu window
            setVisible(false);
        }
    }

    public static void main(String[] args) {
        new MenuTest();
    }
}
