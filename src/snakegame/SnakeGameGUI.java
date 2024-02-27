/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package snakegame;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.table.*;

/**
 *
 * @author ThetNaingSoe
 */
public class SnakeGameGUI {
    public MenuGUI panel;
    public JMenuBar gameMenu;
    public JMenu menu;
    public JMenuBar startGameMenuBar;
    public JMenu startGameMenu; //left corner
    public JFrame highscoresFrame;
    public JFrame gameFrame;
    public JFrame menuFrame;
    public SnakeDB scoresDb;
    public JButton newGameButton;
    public JButton scoresButton;
    public SnakeGameGUI() throws SQLException,HeadlessException,SQLException{
        panel = new MenuGUI(new GridBagLayout());
        scoresDb = new SnakeDB ();
        newGameButton = new JButton("New game");
        scoresButton = new JButton("Scores");
        menuFrame = new JFrame("Start Game Menu");
        createHighScoresFrame();
        menuFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.insets = new Insets(0, 0, 35, 280);
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        panel.add(newGameButton, gridBagConstraints);
        gridBagConstraints.gridy = 1;
        panel.add(scoresButton, gridBagConstraints);
        menuFrame.add(panel);
        startGameMenuBar = new JMenuBar();
        menuFrame.setJMenuBar(startGameMenuBar);
        startGameMenu = new JMenu("Menu");
        JMenuItem restartBtn = new JMenuItem("Restart");
        JMenuItem exit = new JMenuItem("Exit");
        startGameMenu.add(restartBtn);
        startGameMenu.add(exit);
        startGameMenuBar.add(startGameMenu);
        menuFrame.setSize(800, 700);
        menuFrame.setResizable(true);
        menuFrame.setLocationRelativeTo(null);
        menuFrame.setVisible(true);
        addEventListeners( exit , restartBtn, newGameButton, scoresButton );
    }
    private void addEventListeners(JMenuItem exit , JMenuItem restart, JButton newGameButton, JButton scoresButton){
        exit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        restart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                menuFrame.dispose();
                menuFrame.setVisible(true);
            }
        });
        newGameButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent event) {
                createGameFrame();
                gameFrame.setVisible(true);
            }
        });
        scoresButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                if (gameFrame != null) {
                    gameFrame.dispose();
                }
                try {
                    createHighScoresFrame();
                    highscoresFrame.setVisible(true);
                } catch (HeadlessException ex) {
                    System.out.println(ex);
                }
            }
        });
    }
    private void createHighScoresFrame() {
        try {
            highscoresFrame = new JFrame("Highscores");
            JTable scoreTable = new JTable(scoresDb.getDataTable(), scoresDb.getColumnNamesArray());
            scoreTable.setEnabled(false); //you can't select the table or modify
            scoreTable.setRowHeight(50);
            JScrollPane sp = new JScrollPane(scoreTable);
            TableColumnModel columnModel = scoreTable.getColumnModel();
            DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer();
            cellRenderer.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
            columnModel.getColumn(2).setCellRenderer(cellRenderer);
            columnModel.getColumn(1).setCellRenderer(cellRenderer);
            columnModel.getColumn(0).setCellRenderer(cellRenderer);
            highscoresFrame.add(sp);
            highscoresFrame.setSize(SnakePanel.WIDTH, SnakePanel.HEIGHT);
            highscoresFrame.setResizable(true);
            highscoresFrame.setLocationRelativeTo(null);
        } catch (SQLException ex) {
            System.out.println(ex);
        }
    }
    public void createGameFrame() {
        gameFrame = new JFrame("Snake Game");
        gameFrame.add(new SnakePanel(this));
        gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gameMenu = new JMenuBar();
        gameFrame.setJMenuBar(gameMenu);
        menu = new JMenu("Menu");
        JMenuItem restart = new JMenuItem("Restart");
        restart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gameFrame.dispose();
                createGameFrame();
                gameFrame.setVisible(true);
            }
        });
        menu.add(restart);
        JMenuItem main = new JMenuItem("Start Menu");
        main.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gameFrame.dispose();
            }
        });
        menu.add(main);
        gameMenu.add(menu);
        gameFrame.setSize(SnakePanel.WIDTH + 50 , SnakePanel.HEIGHT + 80);
        gameFrame.setResizable(true);
        gameFrame.setLocationRelativeTo(null);
    }
}
