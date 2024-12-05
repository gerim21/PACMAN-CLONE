package pacman;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Instructions extends JPanel {

    private static final long serialVersionUID = 1L;
    private Image backgroundImage;

    public Instructions() {
        backgroundImage = new ImageIcon(getClass().getClassLoader().getResource("images/InstructionsBG.png")).getImage();
        setLayout(null);
        
        JButton btnReturn = new JButton();
        btnReturn.setIcon(new ImageIcon(Instructions.class.getResource("/images/Return.png")));
        btnReturn.setBounds(775, 525, 153, 49);
        btnReturn.setBorderPainted(false);
        btnReturn.setContentAreaFilled(false);
        btnReturn.setFocusPainted(false);
        btnReturn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        add(btnReturn);
        
        btnReturn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame startFrame = new Start(); 
                startFrame.setSize(960,600);
                startFrame.setVisible(true);
                startFrame.setLocationRelativeTo(null);
                SwingUtilities.getWindowAncestor(Instructions.this).dispose();
            }
        });

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
}