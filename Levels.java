package pacman;

import java.awt.*;
import javax.swing.*;

public class Levels extends JPanel {

    private static final long serialVersionUID = 1L;
    private Image backgroundImage;

    public Levels() {
        backgroundImage = new ImageIcon(getClass().getClassLoader().getResource("images/LevelsBG.png")).getImage();

        setLayout(null);
       
        
        JButton btnReturn = new JButton();
        btnReturn.setIcon(new ImageIcon(Levels.class.getResource("/images/Return.png")));
        btnReturn.setFocusPainted(false);
        btnReturn.setContentAreaFilled(false);
        btnReturn.setBorderPainted(false);
        btnReturn.setBounds(404, 392, 153, 49);
        btnReturn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        add(btnReturn);

        JButton btnEasy = new JButton();
        btnEasy.setIcon(new ImageIcon(getClass().getClassLoader().getResource("images/Easy.png")));
        btnEasy.setBounds(404, 215, 153, 49);
        btnEasy.setBorderPainted(false);
        btnEasy.setContentAreaFilled(false);
        btnEasy.setFocusPainted(false);
        btnEasy.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        add(btnEasy);

        JButton btnMedium = new JButton();
        btnMedium.setIcon(new ImageIcon(getClass().getClassLoader().getResource("images/Medium.png")));
        btnMedium.setContentAreaFilled(false);
        btnMedium.setBorderPainted(false);
        btnMedium.setBounds(404, 274, 153, 49);
        btnMedium.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        add(btnMedium);

        JButton btnHard = new JButton();
        btnHard.setIcon(new ImageIcon(getClass().getClassLoader().getResource("images/Hard.png")));
        btnHard.setFocusPainted(false);
        btnHard.setContentAreaFilled(false);
        btnHard.setBorderPainted(false);
        btnHard.setBounds(404, 333, 153, 49);
        btnHard.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        add(btnHard);

        btnEasy.addActionListener(e -> switchToLevel("Easy"));
        btnMedium.addActionListener(e -> switchToLevel("Medium"));
        btnHard.addActionListener(e -> switchToLevel("Hard"));
        btnReturn.addActionListener(e -> switchToLevel("Return"));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        } else {
            System.err.println("Error: Unable to load the background image.");
        }
    }

    private void switchToLevel(String difficulty) {
        JFrame levelFrame;
        switch (difficulty) {
            case "Easy":
                levelFrame = new JFrame("Pac-Man Game - Easy Level");
                levelFrame.setContentPane(new EasyLvl());
                break;
            case "Medium":
                levelFrame = new JFrame("Pac-Man Game - Medium Level");
                levelFrame.setContentPane(new MediumLvl());
                break;
            case "Hard":
                levelFrame = new JFrame("Pac-Man Game - Hard Level");
                levelFrame.setContentPane(new HardLvl());
                break;
            case "Return":
                levelFrame = new Start();
                break;   
            default:
                throw new IllegalArgumentException("Unknown level difficulty: " + difficulty);
        }

        levelFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        levelFrame.setSize(570, 600);
        levelFrame.setVisible(true);
        levelFrame.setLocationRelativeTo(null);
        SwingUtilities.getWindowAncestor(this).dispose();
    }
}