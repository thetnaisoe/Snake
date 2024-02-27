/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package snakegame;

/**
 *
 * @author ThetNaingSoe
 */
public class SnakeGame {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try{
            new SnakeGameGUI();
        }catch(Exception e ){
            System.out.println(e);
        }
    }
    
}
