package pacman;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.eclipse.wb.swing.FocusTraversalOnArray;

public class WinScreen extends JFrame {
    public WinScreen() {
        getContentPane().setForeground(new Color(0, 0, 0));
        setTitle("You Win!");
        setSize(534, 498);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setLayout(null);
        
        JButton btnReturn = new JButton();
        btnReturn.setIcon(new ImageIcon(getClass().getClassLoader().getResource("images/Return.png")));
        btnReturn.setBounds(185, 280, 153, 49);
        btnReturn.setBorderPainted(false);
        btnReturn.setContentAreaFilled(false);
        btnReturn.setFocusPainted(false);
        btnReturn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        btnReturn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Start(); 
                dispose();
            }
        });
        
        JLabel lblReturnToMain = new JLabel("Return to Main Menu to play again.");
        lblReturnToMain.setForeground(Color.WHITE);
        lblReturnToMain.setFont(new Font("VCR OSD Mono", Font.PLAIN, 14));
        lblReturnToMain.setBounds(128, 214, 293, 56);
        getContentPane().add(lblReturnToMain);
        
        JLabel lblWinScreen = new JLabel("You have finished the hard level.  ");
        lblWinScreen.setForeground(Color.WHITE);
        lblWinScreen.setFont(new Font("VCR OSD Mono", Font.PLAIN, 14));
        lblWinScreen.setBounds(138, 194, 300, 49);
        getContentPane().add(lblWinScreen);
        
        JLabel lblScore = new JLabel("Score: 221");
        lblScore.setFont(new Font("VCR OSD Mono", Font.BOLD, 26));
        lblScore.setForeground(new Color(255, 255, 255));
        lblScore.setBounds(183, 146, 169, 49);
        getContentPane().add(lblScore);
                
        getContentPane().add(btnReturn);

        JLabel background = new JLabel();
        background.setIcon(new ImageIcon(WinScreen.class.getResource("/images/WinScreen.png")));
        background.setBounds(0, 0, getWidth(), getHeight());
        background.setHorizontalAlignment(SwingConstants.CENTER);
        background.setVerticalAlignment(SwingConstants.CENTER);
        getContentPane().add(background);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            WinScreen winScreen = new WinScreen();
            winScreen.setVisible(true);
        });
    }
}