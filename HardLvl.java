package pacman;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.util.*;
import javax.swing.SwingUtilities;
import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

@SuppressWarnings("serial")
public class HardLvl extends JPanel implements ActionListener {
    private static final int CHASING_DISTANCE = 0;
	private ArrayList<Wall> walls;
    private final Queue<Point> pathQueue = new LinkedList<>(); 
    private final int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}}; 

    private boolean pathFound = false;

    private void initializeWalls() {
        walls = new ArrayList<>();
        walls.add(new Wall(0, 0, BLOCK_SIZE, SCREEN_SIZE)); // Left wall
        walls.add(new Wall(SCREEN_SIZE - BLOCK_SIZE, 0, BLOCK_SIZE, SCREEN_SIZE)); 
        walls.add(new Wall(0, 0, SCREEN_SIZE, BLOCK_SIZE)); // Top wall
        walls.add(new Wall(0, SCREEN_SIZE - BLOCK_SIZE, SCREEN_SIZE, BLOCK_SIZE)); 
    }

    private Dimension d;
    private final Font smallFont = new Font("Arial", Font.BOLD, 14);
    private boolean inGame = false;
    private boolean dying = false;
    private final int BLOCK_SIZE = 36;
    private final int N_BLOCKS = 15;
    private final int SCREEN_SIZE = N_BLOCKS * BLOCK_SIZE;
    private final int MAX_GHOSTS = 12;
    private final int PACMAN_SPEED = 3;

    private int N_GHOSTS = 6;
    private int lives, score;
    private int[] dx, dy;
    private int[] ghost_x, ghost_y, ghost_dx, ghost_dy, ghostSpeed;

    private Image heart, ghost;
    private Image up, down, left, right;

    private int pacman_x, pacman_y, pacmand_x, pacmand_y;
    private int req_dx, req_dy;

    private final short levelData[] = {
        19, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 22,
        17, 16, 24, 24, 24, 32, 16, 24, 16, 16, 24, 24, 24, 16, 20,
        17, 20,  0,  0,  0, 17, 20,  0, 17, 20,  0,  0,  0, 17, 20,
        17, 16, 18, 22,  0, 17, 20,  0, 17, 20,  0, 19, 18, 16, 20,
        17, 16, 24, 28,  0, 17, 20,  0, 17, 20,  0, 25, 24, 16, 20,
        17, 20,  0,  0,  0, 17, 20,  0, 17, 20,  0,  0,  0, 17, 20,
        17, 16, 18, 18, 18, 16, 32, 18, 16, 16, 18, 18, 18, 16, 20,
        17, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 32, 16, 20,
        17, 16, 24, 24, 24, 16, 16, 24, 16, 16, 24, 24, 24, 16, 20,
        17, 20,  0,  0,  0, 17, 20,  0, 17, 20,  0,  0,  0, 17, 20,
        17, 16, 18, 22,  0, 17, 20,  0, 17, 20,  0, 19, 18, 16, 20,
        17, 16, 24, 28,  0, 17, 20,  0, 17, 20,  0, 25, 24, 16, 20,
        17, 20,  0,  0,  0, 17, 20,  0, 17, 20,  0,  0,  0, 17, 20,
        17, 32, 18, 18, 18, 16, 16, 18, 16, 16, 18, 18, 18, 16, 20,
        25, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 28
    };

    private short[] screenData;
    private Timer timer;

    // Score effect related
    private ArrayList<ScoreEffect> scoreEffects; 

    public HardLvl() {
        setBackground(new Color(0, 0, 0));
        loadImages();
        initVariables();
        addKeyListener(new TAdapter());
        setFocusable(true);
        inGame = true; 
        initGame(); 
        scoreEffects = new ArrayList<>(); 
    }

    private void loadImages() {
        down = new ImageIcon(getClass().getResource("/images/down.gif")).getImage();
        up = new ImageIcon(getClass().getResource("/images/up.gif")).getImage();
        left = new ImageIcon(getClass().getResource("/images/left.gif")).getImage();
        right = new ImageIcon(getClass().getResource("/images/right.gif")).getImage();
        ghost = new ImageIcon(getClass().getResource("/images/ghost.gif")).getImage();
        heart = new ImageIcon(getClass().getResource("/images/heart.png")).getImage();
    }

    private void initVariables() {
        screenData = new short[N_BLOCKS * N_BLOCKS];
        d = new Dimension(400, 400);
        ghost_x = new int[MAX_GHOSTS];
        ghost_dx = new int[MAX_GHOSTS];
        ghost_y = new int[MAX_GHOSTS];
        ghost_dy = new int[MAX_GHOSTS];
        ghostSpeed = new int[MAX_GHOSTS];
        dx = new int[4];
        dy = new int[4];

        timer = new Timer(40, this);
        timer.start();
        initializeWalls();
    }

    private void playGame(Graphics2D g2d) {
        if (dying) {
            death();
        } else {
            movePacman();
            drawPacman(g2d);
            moveGhosts(g2d);
            checkMaze();

            
            Iterator<ScoreEffect> iterator = scoreEffects.iterator();
            while (iterator.hasNext()) {
                ScoreEffect effect = iterator.next();
                effect.update();
                effect.draw(g2d);
                if (effect.isFinished()) {
                    iterator.remove();
                }
            }
        }
    }

    private void drawScore(Graphics2D g) {
        g.setFont(smallFont);
        g.setColor(new Color(5, 181, 79));
        String s = "Score: " + score;
        g.drawString(s, SCREEN_SIZE / 2 + 96, SCREEN_SIZE + 16);

        for (int i = 0; i < lives; i++) {
            g.drawImage(heart, i * 28 + 8, SCREEN_SIZE + 1, this);
        }
    }
    
    private boolean dialogShown = false;
    
    private void checkMaze() {
        int i = 0;
        boolean finished = true;

        while (i < N_BLOCKS * N_BLOCKS && finished) {
            if ((screenData[i]) != 0) {
                finished = false;
            }
            i++;
        }

        if (finished) {
            score += 50;

            if (N_GHOSTS < MAX_GHOSTS) {
                N_GHOSTS++;
            }

            initLevel();
        }

        if (score >= 221 && !dialogShown) {
            dialogShown = true; 
            showWinDialog();
        }
    }

    private void showWinDialog() {
        SwingUtilities.invokeLater(() -> {
            new WinScreen().setVisible(true);

            JFrame currentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            if (currentFrame != null) {
                currentFrame.dispose();
            }
        });
    }

    private void death() {
        String filepath = "latest pacman update/src/sounds/pacman_death.wav";
        PlaySound(filepath);    
        lives--;
        if (lives == 0) {
            inGame = false;
        }
        continueLevel();
    }

    private void moveGhosts(Graphics2D g2d) {
        int pos;
        int count;

        for (int i = 0; i < N_GHOSTS; i++) {
            // Calculate Manhattan distance to Pac-Man
            int distanceToPacman = Math.abs(pacman_x - ghost_x[i]) + Math.abs(pacman_y - ghost_y[i]);
            
            // If Pac-Man is within chasing distance, modify the ghost's direction
            if (distanceToPacman < CHASING_DISTANCE) {
                // Directly set ghost direction towards Pac-Man
                if (pacman_x < ghost_x[i]) {
                    ghost_dx[i] = -1; // Move left
                    ghost_dy[i] = 0;
                } else if (pacman_x > ghost_x[i]) {
                    ghost_dx[i] = 1; // Move right
                    ghost_dy[i] = 0;
                } else if (pacman_y < ghost_y[i]) {
                    ghost_dy[i] = -1; // Move up
                    ghost_dx[i] = 0;
                } else if (pacman_y > ghost_y[i]) {
                    ghost_dy[i] = 1; // Move down
                    ghost_dx[i] = 0;
                }
            } else {
                if (ghost_x[i] % BLOCK_SIZE == 0 && ghost_y[i] % BLOCK_SIZE == 0) {
                    pos = ghost_x[i] / BLOCK_SIZE + N_BLOCKS * (int) (ghost_y[i] / BLOCK_SIZE);
                    count = 0;

                    if ((screenData[pos] & 1) == 0 && ghost_dx[i] != 1) {
                        dx[count] = -1;
                        dy[count] = 0;
                        count++;
                    }

                    if ((screenData[pos] & 2) == 0 && ghost_dy[i] != 1) {
                        dx[count] = 0;
                        dy[count] = -1;
                        count++;
                    }

                    if ((screenData[pos] & 4) == 0 && ghost_dx[i] != -1) {
                        dx[count] = 1;
                        dy[count] = 0;
                        count++;
                    }

                    if ((screenData[pos] & 8) == 0 && ghost_dy[i] != -1) {
                        dx[count] = 0;
                        dy[count] = 1;
                        count++;
                    }

                    if (count == 0) {
                        if ((screenData[pos] & 15) == 15) {
                            ghost_dx[i] = 0;
                            ghost_dy[i] = 0;
                        } else {
                            ghost_dx[i] = -ghost_dx[i];
                            ghost_dy[i] = -ghost_dy[i];
                        }
                    } else {
                        count = (int) (Math.random() * count);
                        ghost_dx[i] = dx[count];
                        ghost_dy[i] = dy[count];
                    }
                }
            }

            ghost_x[i] += ghost_dx[i] * ghostSpeed[i];
            ghost_y[i] += ghost_dy[i] * ghostSpeed[i];
            drawGhost(g2d, ghost_x[i] + 1, ghost_y[i] + 1);

            if (pacman_x > (ghost_x[i] - 12) && pacman_x < (ghost_x[i] + 12)
                    && pacman_y > (ghost_y[i] - 12) && pacman_y < (ghost_y[i] + 12)
                    && inGame) {
                dying = true;
            }
        }
    }

    private void drawGhost(Graphics2D g2d, int x, int y) {
        g2d.drawImage(ghost, x + 5, y + 5, this);
    }

    private void movePacman() {
        int pos;
        short ch;

        if (pacman_x % BLOCK_SIZE == 0 && pacman_y % BLOCK_SIZE == 0) {
            pos = pacman_x / BLOCK_SIZE + N_BLOCKS * (int) (pacman_y / BLOCK_SIZE);
            ch = screenData[pos];

            if ((ch & 16) != 0) { // Regular pellet
                screenData[pos] = (short) (ch & 15);
                score++;
                // Create a score effect
                scoreEffects.add(new ScoreEffect(pacman_x + 14, pacman_y + 20, "+1", 30));
                PlaySound("latest pacman update/src/sounds/pacman_chomp.wav");
            }
            if ((ch & 32) != 0) { // Big pellet
                screenData[pos] = (short) (ch & ~32);
                score += 10; 
                // Create a score effect
                scoreEffects.add(new ScoreEffect(pacman_x + 14, pacman_y + 20, "+10", 30));
                PlaySound("latest pacman update/src/sounds/pacman_chomp.wav");
            }

            if (req_dx != 0 || req_dy != 0) {
                if (!((req_dx == -1 && req_dy == 0 && (ch & 1) != 0) 
                      || (req_dx == 1 && req_dy == 0 && (ch & 4) != 0) 
                      || (req_dx == 0 && req_dy == -1 && (ch & 2) != 0) 
                      || (req_dx == 0 && req_dy == 1 && (ch & 8) != 0))) {
                    pacmand_x = req_dx;
                    pacmand_y = req_dy;
                }
            }

            if ((pacmand_x == -1 && pacmand_y == 0 && (ch & 1) != 0)   
                    || (pacmand_x == 1 && pacmand_y == 0 && (ch & 4) != 0) 
                    || (pacmand_x == 0 && pacmand_y == -1 && (ch & 2) != 0) 
                    || (pacmand_x == 0 && pacmand_y == 1 && (ch & 8) != 0)) { 
                pacmand_x = 0;
                pacmand_y = 0;
            }
        }

        pacman_x = pacman_x + PACMAN_SPEED * pacmand_x;
        pacman_y = pacman_y + PACMAN_SPEED * pacmand_y;
    }

    private void PlaySound(String filepath) {
        try {
            // Open the audio input stream
            File soundFile = new File(filepath);
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(soundFile);
            
            // Get a sound clip resource
            Clip clip = AudioSystem.getClip();

            // Open the audio clip and load samples from the audio input stream
            clip.open(audioInputStream);

            // Start playing the sound clip
            clip.start();
        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    private void drawPacman(Graphics2D g2d) {
        if (req_dx == -1) {
            g2d.drawImage(left, pacman_x + 7, pacman_y + 6, this);
        } else if (req_dx == 1) {
            g2d.drawImage(right, pacman_x + 7, pacman_y + 6, this);
        } else if (req_dy == -1) {
            g2d.drawImage(up, pacman_x + 7, pacman_y + 6, this);
        } else {
            g2d.drawImage(down, pacman_x + 7, pacman_y + 6, this);
        }
    }

    private void checkCollisions() {
        for (Wall wall : walls) {
            if (wall.collidesWith(pacman_x, pacman_y)) {
                pacmand_x = 0;
                pacmand_y = 0;
                break;
            }
        }
    }

    private void drawMaze(Graphics2D g2d) {
        short i = 0;
        int x, y;

        for (y = 0; y < SCREEN_SIZE; y += BLOCK_SIZE) {
            for (x = 0; x < SCREEN_SIZE; x += BLOCK_SIZE) {
                g2d.setColor(new Color(0, 72, 251));
                g2d.setStroke(new BasicStroke(5));

                if ((levelData[i] == 0)) {
                    g2d.fillRect(x, y, BLOCK_SIZE, BLOCK_SIZE);
                }

                if ((screenData[i] & 1) != 0) {
                    g2d.drawLine(x, y, x, y + BLOCK_SIZE - 1);
                }

                if ((screenData[i] & 2) != 0) {
                    g2d.drawLine(x, y, x + BLOCK_SIZE - 1, y);
                }

                if ((screenData[i] & 4) != 0) {
                    g2d.drawLine(x + BLOCK_SIZE - 1, y, x + BLOCK_SIZE - 1,
                            y + BLOCK_SIZE - 1);
                }

                if ((screenData[i] & 8) != 0) {
                    g2d.drawLine(x, y + BLOCK_SIZE - 1, x + BLOCK_SIZE - 1,
                            y + BLOCK_SIZE - 1);
                }

                if ((screenData[i] & 16) != 0) {
                    g2d.setColor(new Color(255, 255, 255));
                    g2d.fillOval(x + 14, y + 14, 6, 6);
                }
                // Draw big pellet
                if ((screenData[i] & 32) != 0) { // Check for big pellet
                    g2d.setColor(new Color(255, 215, 0)); // Gold color for big pellet
                    g2d.fillOval(x + 7, y + 6, 16, 16); // Larger circle for big pellet
                }
                
                i++;
            }
        }
    }

    private void initGame() {
        lives = 3;
        score = 0;
        initLevel();
        N_GHOSTS = 6;
        for (int i = 0; i < N_GHOSTS; i++) {
            ghost_x[i] = 2 * BLOCK_SIZE; // Start position
            ghost_y[i] = 8 * BLOCK_SIZE;
            ghost_dx[i] = 1; 
            ghost_dy[i] = 0;
            ghostSpeed[i] = 2;
        }
        pacman_x = 7 * BLOCK_SIZE;  // Start position
        pacman_y = 8 * BLOCK_SIZE;
        pacmand_x = 0; 
        pacmand_y = 0;
        req_dx = 0;
        req_dy = 0;
        dying = false;
    }

    private void initLevel() {
        int i;

        for (i = 0; i < N_BLOCKS * N_BLOCKS; i++) {
            screenData[i] = levelData[i];
        }

        continueLevel();
    }

    private void continueLevel() {
        pacman_x = 7 * BLOCK_SIZE;  // Start position
        pacman_y = 8 * BLOCK_SIZE;
        pacmand_x = 0; 
        pacmand_y = 0;
        req_dx = 0; 
        req_dy = 0;
        dying = false;
    }

    public class Wall {
        private int x; 
        private int y;
        private int width; 
        private int height; 

        public Wall(int x, int y, int width, int height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        public boolean collidesWith(int pacmanX, int pacmanY) {
            return pacmanX < x + width && pacmanX + BLOCK_SIZE > x &&
                    pacmanY < y + height && pacmanY + BLOCK_SIZE > y;
        }
    }

    // ScoreEffect class for displaying the score effect
    class ScoreEffect {
        private int x, y;
        private int duration; // Duration for how long the score text will be displayed
        private int elapsed;  // Elapsed time
        private String scoreText;

        public ScoreEffect(int x, int y, String scoreText, int duration) {
            this.x = x;
            this.y = y;
            this.scoreText = scoreText;
            this.duration = duration;
            this.elapsed = 0;
        }

        public void update() {
            elapsed++;
            y -= 1; // Move the effect upward
        }

        public void draw(Graphics2D g) {
            if (elapsed < duration) {
                g.setColor(Color.YELLOW);
                g.setFont(new Font("Arial", Font.BOLD, 14));
                g.drawString(scoreText, x, y);
            }
        }

        public boolean isFinished() {
            return elapsed >= duration;
        }
    }

    private void openStartScreen() {
        SwingUtilities.invokeLater(() -> {
            Start startFrame = new Start();
            startFrame.setVisible(true);
            startFrame.setLocationRelativeTo(null);
            startFrame.setSize(960,600);
            JFrame currentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            if (currentFrame != null) {
                currentFrame.dispose(); 
            }
        });
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        if (inGame) {
            drawMaze(g2d);
            drawScore(g2d);
            playGame(g2d);
        } else {
            if (lives == 0) {
                g2d.setColor(new Color(0, 0, 0, 150));
                g2d.fillRect(0, 0, getWidth(), getHeight());

                g2d.setColor(Color.RED);
                g2d.setFont(new Font("Arial", Font.BOLD, 15));

                String gameOverMessage = "Game Over! Your score: " + score;
                int messageWidth = g2d.getFontMetrics().stringWidth(gameOverMessage);
                g2d.drawString(gameOverMessage, (getWidth() - messageWidth) / 2 , getHeight() / 2);

                String restartMessage = "Press [Space] to restart or [Enter] to return to the menu";
                messageWidth = g2d.getFontMetrics().stringWidth(restartMessage);
                g2d.drawString(restartMessage, (getWidth() - messageWidth) / 2, getHeight() / 2 + 28);
            } else {
                openStartScreen();
            }
        }

        Toolkit.getDefaultToolkit().sync();
    }

    class TAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();

            if (inGame) {
                if (key == KeyEvent.VK_LEFT) {
                    req_dx = -1;
                    req_dy = 0;
                } else if (key == KeyEvent.VK_RIGHT) {
                    req_dx = 1;
                    req_dy = 0;
                } else if (key == KeyEvent.VK_UP) {
                    req_dx = 0;
                    req_dy = -1;
                } else if (key == KeyEvent.VK_DOWN) {
                    req_dx = 0;
                    req_dy = 1;
                } else if (key == KeyEvent.VK_ESCAPE) {
                    inGame = false;
                }
            } else if (key == KeyEvent.VK_SPACE) {
                inGame = true;
                initGame();
            } else if (key == KeyEvent.VK_ENTER) {
                openStartScreen();
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        repaint();
    }

    public void startGameHardLvl() {
        inGame = true;
        initGame();
        repaint();
    }
}