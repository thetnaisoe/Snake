/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package snakegame;
import java.awt.*;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;
import javax.swing.*;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author ThetNaingSoe
 */
public class SnakePanel extends JPanel implements ActionListener{
    public static final int WIDTH = 1000;
    public static final int HEIGHT = 600;
    public static final int OBJECT_SIZE = 25;
    public static final int GAME_UNITS = (WIDTH * HEIGHT) / (OBJECT_SIZE * OBJECT_SIZE);
    public static final int TIME = 75;
    private final int SnakeXCoordinates[] = new int[GAME_UNITS];
    private final int SnakeYCoordinates[] = new int[GAME_UNITS];
    private int bodyParts = 2;
    private int score;
    private int appleX;
    private int appleY;
    private char direction = 'D';
    private boolean isRunning = false;
    private Timer timer;
    private Random random;
    private int level = 1;
    private List<Rock> rocks;
    private int prevoiusLevel = 2;
    private long start;
    private long current;
    private SnakeDB highScoresDB;
    private SnakeGameGUI sgui;
    public SnakePanel(SnakeGameGUI sgui) {
        this.sgui = sgui;
        random = new Random();
        setToRadomDirection();
   
        this.setBackground(new Color(205, 133, 63));
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        this.rocks = new ArrayList<>();
        startGame();
    }
    private void setToRadomDirection() {
        int randNum = random.nextInt(4);
        switch (randNum) {
            case 0:
                direction = 'W';
                break;
            case 1:
                direction = 'A';
                break;
            case 2:
                direction = 'S';
                break;
            case 3:
                direction = 'D';
                break;
        }
    }
    private void restart() {
        this.sgui.gameFrame.dispose();
        sgui.createGameFrame();
        this.sgui.gameFrame.setVisible(true);
    }
    public void startGame() {
        newApple();
        isRunning = true;
        timer = new Timer(100, this);
        timer.start();
        start = System.nanoTime();
        current = System.nanoTime();
        SnakeXCoordinates[0] = WIDTH / 2;
        SnakeYCoordinates[0] = HEIGHT / 2;
    }
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }
    private boolean doesCollideWithSnake(int xCoord, int yCoord) {
        for (int i = 0; i < bodyParts; i++) {
            if (SnakeXCoordinates[i] == xCoord && SnakeYCoordinates[i] == yCoord) {
                return true;
            }
        }
        return false;
    }
    private boolean doesCollideWithRocks(int xCoord, int yCoord) {
        for (int i = 0; i < rocks.size(); i++) {
            if (rocks.get(i).x == xCoord && rocks.get(i).y == yCoord) {
                return true;
            }
        }
        return false;
    }
    private void generateObstacles() {
        if (prevoiusLevel == level) {
            int numOfRocks = 5;
            Random rand = new Random();
            for (int i = 0; i < numOfRocks; i++) {
                int xCoord = rand.nextInt((int) (WIDTH / OBJECT_SIZE)) * OBJECT_SIZE;
                int yCoord = rand.nextInt((int) (HEIGHT / OBJECT_SIZE)) * OBJECT_SIZE;
                // check collisions with snake && check collisions with the previously added
                // rocks
                while (doesCollideWithSnake(xCoord, yCoord) || doesCollideWithRocks(xCoord, yCoord)) {
                    xCoord = rand.nextInt((int) (WIDTH / OBJECT_SIZE)) * OBJECT_SIZE;
                    yCoord = rand.nextInt((int) (HEIGHT / OBJECT_SIZE)) * OBJECT_SIZE;
                }
                rocks.add(new Rock(xCoord, yCoord));
            }
            prevoiusLevel++;
        }
    }
    public void draw(Graphics g) {
        if (isRunning) {
            g.setColor(Color.red);
            g.fillOval(appleX, appleY, OBJECT_SIZE, OBJECT_SIZE);
            for (int i = 0; i < bodyParts; i++) {
                if (i == 0) {
                    g.setColor(new Color(169, 169, 169));
                    g.fillRect(SnakeXCoordinates[i], SnakeYCoordinates[i], OBJECT_SIZE, OBJECT_SIZE);
                } else {
                    g.setColor(new Color(128, 128, 128));
                    g.fillRect(SnakeXCoordinates[i], SnakeYCoordinates[i], OBJECT_SIZE, OBJECT_SIZE);
                }
            }
            g.setColor(Color.green);
            g.setFont(new Font("Sans serif fonts", Font.BOLD, 30));
            current = System.nanoTime();
            g.drawString("Score: " + score, 100, 25);
            g.drawString("Level: " + level, 500, 25);
            g.drawString("Elapsed Time: " + ((current - start) / 1_000_000_000), 700, 25);
            if (level >= 2) {
                generateObstacles();
                g.setColor(Color.black);
                for (int i = 0; i < rocks.size(); i++) {
                    g.fillRect(rocks.get(i).x, rocks.get(i).y, OBJECT_SIZE, OBJECT_SIZE);
                }
            }
        } else {
            gameOver();
        }
    }
    public void newApple() {
        appleX = random.nextInt((int) (WIDTH / OBJECT_SIZE)) * OBJECT_SIZE;
        appleY = random.nextInt((int) (HEIGHT / OBJECT_SIZE)) * OBJECT_SIZE;
        // apple doesn't collide with the rocks || apple doesn't collide with the snake
        while (doesCollideWithRocks(appleX, appleY) || doesCollideWithSnake(appleX, appleY)) {
            appleX = random.nextInt((int) (WIDTH / OBJECT_SIZE)) * OBJECT_SIZE;
            appleY = random.nextInt((int) (HEIGHT / OBJECT_SIZE)) * OBJECT_SIZE;
        }
    }
    public void move() {
        for (int i = bodyParts; i > 0; i--) {
            SnakeXCoordinates[i] = SnakeXCoordinates[i - 1];
            SnakeYCoordinates[i] = SnakeYCoordinates[i - 1];
        }
        if (direction == 'W') {
            SnakeYCoordinates[0] = SnakeYCoordinates[0] - OBJECT_SIZE;
        }
        if (direction == 'S') {
            SnakeYCoordinates[0] = SnakeYCoordinates[0] + OBJECT_SIZE;
        }
        if (direction == 'A') {
            SnakeXCoordinates[0] = SnakeXCoordinates[0] - OBJECT_SIZE;
        }
        if (direction == 'D') {
            SnakeXCoordinates[0] = SnakeXCoordinates[0] + OBJECT_SIZE;
        }
    }
    public void checkApple() {
        if ((SnakeXCoordinates[0] == appleX) && (SnakeYCoordinates[0] == appleY)) {
            bodyParts++;
            score++;
            switch (score) {
                case 2:
                    level = 2;
                    setToRadomDirection();
                    SnakeXCoordinates[0] = WIDTH / 2;
                    SnakeYCoordinates[0] = HEIGHT / 2;
                    break;
                case 3:
                    level = 3;
                    break;
                case 4:
                    level = 4;
                    setToRadomDirection();
                    SnakeXCoordinates[0] = WIDTH / 2;
                    SnakeYCoordinates[0] = HEIGHT / 2;
                    break;
                case 5:
                    level = 5;
                    setToRadomDirection();
                    SnakeXCoordinates[0] = WIDTH / 2;
                    SnakeYCoordinates[0] = HEIGHT / 2;
                    break;
                case 6:
                    level = 6;
                    break;
                case 7:
                    level = 7;
                    setToRadomDirection();
                    SnakeXCoordinates[0] = WIDTH / 2;
                    SnakeYCoordinates[0] = HEIGHT / 2;
                    break;
                case 8:
                    level = 8;
                    break;
                case 9:
                    level = 9;
                    setToRadomDirection();
                    SnakeXCoordinates[0] = WIDTH / 2;
                    SnakeYCoordinates[0] = HEIGHT / 2;
                    break;
                case 10:
                    level = 10;
                    break;
                case 11:
                    isRunning = false;
                    String pName = JOptionPane.showInputDialog(this, "Enter your name: ", "You won!", JOptionPane.INFORMATION_MESSAGE);
                    if (pName != null) {
                        try {
                            new SnakeDB().putHighScore(pName, score);
                        } catch (Exception e) {
                        }
                    }
                    int choice = JOptionPane.showConfirmDialog(this, "Restart?", "Game Over!", JOptionPane.INFORMATION_MESSAGE);
                    if (choice == JOptionPane.OK_OPTION) {
                        restart();
                    } else {
                        System.exit(0);
                    }
                    break;
            }
            newApple();
        }
    }
    public void checkCollisions() {
        // checking if the snake collides with itself
        for (int i = bodyParts; i > 0; i--) {
            if ((SnakeXCoordinates[0] == SnakeXCoordinates[i]) && (SnakeYCoordinates[0] == SnakeYCoordinates[i])) {
                isRunning = false;
            }
        }
        // checking if the snake collides with the walls
        if (SnakeXCoordinates[0] < 0 || SnakeXCoordinates[0] > WIDTH || SnakeYCoordinates[0] < 0
                || SnakeYCoordinates[0] > HEIGHT) {
            isRunning = false;
        }
        for (int i = 0; i < bodyParts; i++) {
            if (doesCollideWithRocks(SnakeXCoordinates[i], SnakeYCoordinates[i])) {
                isRunning = false;
            }
        }
        if (!isRunning) {
            timer.stop();
        }
    }
    public void gameOver() {
        isRunning = false;
        String pName = JOptionPane.showInputDialog(this, "Enter your name: ", "Game Over!",
                JOptionPane.INFORMATION_MESSAGE);
        if (pName != null) {
            try {
                new SnakeDB().putHighScore(pName, score);
            } catch (Exception e) {
            }
        }
        int choice = JOptionPane.showConfirmDialog(this, "Restart?", "Game Over!", JOptionPane.INFORMATION_MESSAGE);
        if (choice == JOptionPane.OK_OPTION) {
            restart();
        } else {
            System.exit(0);
        }
       
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        if (isRunning) {
            move();
            checkApple();
            checkCollisions();
        }
        repaint();
    }
    public class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();
            if (key == KeyEvent.VK_A) {
                if (direction != 'D') {
                    direction = 'A';
                }
            }
            if (key == KeyEvent.VK_D) {
                if (direction != 'A') {
                    direction = 'D';
                }
            }
            if (key == KeyEvent.VK_W) {
                if (direction != 'S') {
                    direction = 'W';
                }
            }
            if (key == KeyEvent.VK_S) {
                if (direction != 'W') {
                    direction = 'S';
                }
            }
        }
    }
}
