package pacman;

import java.awt.*;
import javax.sound.sampled.*;
import javax.swing.*;
import java.io.File;

public class Start extends JFrame {
    
    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private Clip backgroundMusicClip; 
    private boolean isMusicPlaying = false; 
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                Start frame = new Start();
                frame.setVisible(true);
                frame.setLocationRelativeTo(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public Start() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 960, 640);

        contentPane = new JPanel() {
            private static final long serialVersionUID = 1L;

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon bgIcon = new ImageIcon(getClass().getClassLoader().getResource("images/MenuBG.png"));
                if (bgIcon != null && bgIcon.getImage() != null) {
                    g.drawImage(bgIcon.getImage(), 0, 0, getWidth(), getHeight(), null);
                } else {
                    System.err.println("Error: Unable to load the background image.");
                }
            }
        };
        
        contentPane.setLayout(null);
        setContentPane(contentPane);

       
        JButton btnPlay = new JButton();
        btnPlay.setIcon(new ImageIcon(getClass().getClassLoader().getResource("images/Play.png")));
        btnPlay.setBounds(404, 350, 153, 49);
        btnPlay.setBorderPainted(false);
        btnPlay.setContentAreaFilled(false);
        btnPlay.setFocusPainted(false);
        btnPlay.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        contentPane.add(btnPlay);

        JButton btnInstructions = new JButton();
        btnInstructions.setIcon(new ImageIcon(getClass().getClassLoader().getResource("images/Instructions.png")));
        btnInstructions.setContentAreaFilled(false);
        btnInstructions.setBorderPainted(false);
        btnInstructions.setBounds(404, 420, 153, 49);
        btnInstructions.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        contentPane.add(btnInstructions);

        JButton btnQuit = new JButton();
        btnQuit.setIcon(new ImageIcon(getClass().getClassLoader().getResource("images/Quit.png")));
        btnQuit.setFocusPainted(false);
        btnQuit.setContentAreaFilled(false);
        btnQuit.setBorderPainted(false);
        btnQuit.setBounds(404, 485, 153, 49);
        btnQuit.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        contentPane.add(btnQuit);

        
        btnPlay.addActionListener(e -> {
            stopBackgroundMusic(); 
            JFrame gameFrame = new JFrame("Pac-Man Game");
            gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            gameFrame.setSize(960, 540);

            Levels levelsPanel = new Levels(); 
            gameFrame.setContentPane(levelsPanel);
            gameFrame.setLocationRelativeTo(null);
            gameFrame.setSize(960, 600);
            gameFrame.setVisible(true);
            this.dispose(); 
        });

        btnQuit.addActionListener(e -> {
            stopBackgroundMusic(); 
            System.exit(0);
        });

        btnInstructions.addActionListener(e -> {
            stopBackgroundMusic(); // Stop music when switching to instructions
            Instructions instructionsPanel = new Instructions();
            JFrame instructionsFrame = new JFrame("Instructions");
            instructionsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            instructionsFrame.setSize(960, 640);
            instructionsFrame.setContentPane(instructionsPanel);
            instructionsFrame.setVisible(true);
            instructionsFrame.setLocationRelativeTo(null);
            this.dispose();
        });

       
        playBackgroundMusic();
    }

    private void playBackgroundMusic() {
        if (!isMusicPlaying) {  
            String bgMusicPath = "latest pacman update/src/sounds/pacman_beginning.wav"; 
            try {
                File musicPath = new File(bgMusicPath);
                if (musicPath.exists()) {
                    AudioInputStream audioInput = AudioSystem.getAudioInputStream(musicPath);
                    backgroundMusicClip = AudioSystem.getClip();
                    backgroundMusicClip.open(audioInput);
                    backgroundMusicClip.loop(Clip.LOOP_CONTINUOUSLY); 
                    backgroundMusicClip.start(); 
                    isMusicPlaying = true; 
                } else {
                    System.out.println("Background music file not found at path: " + bgMusicPath);
                }
            } catch (Exception e) {
                System.out.println("Error playing background music: " + e);
            }
        }
    }

    private void stopBackgroundMusic() {
        if (backgroundMusicClip != null) {
            backgroundMusicClip.stop();
            backgroundMusicClip.close(); 
            backgroundMusicClip = null; 
            isMusicPlaying = false; 
        }
    }
}