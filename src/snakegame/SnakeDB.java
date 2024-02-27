/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package snakegame;
import java.sql.*;
import java.util.*;
import snakegame.SnakeScore;
/**
 *
 * @author ThetNaingSoe
 */
public class SnakeDB {
    private int maxNumOfScores;
    private PreparedStatement insertStatement;
    private PreparedStatement deleteStatement;
    private PreparedStatement defaultStatement;
    private Connection conn;  

    public SnakeDB() throws SQLException{
        this.maxNumOfScores =  10 ;
        String dataBaseURL = "jdbc:mysql://localhost/snakescore?"
                    + "serverTimezone=UTC&user=root&password=Tns15702";  
        String insertQuery = "INSERT INTO HIGHSCORES ( NAME, SCORE) VALUES (?, ?)";
        String deleteQuery = "DELETE FROM HIGHSCORES WHERE SCORE=?";
        String createQuery = "CREATE TABLE IF NOT EXISTS HIGHSCORES (Id int(20) primary key auto_increment, NAME varchar(200) not null, SCORE int(20))";
        this.conn = DriverManager.getConnection(dataBaseURL);
        this.defaultStatement = conn.prepareStatement(createQuery);
        this.insertStatement = conn.prepareStatement(insertQuery);
        this.deleteStatement = conn.prepareStatement(deleteQuery);
    }
    
    private Integer getExistingScore(String playerName) throws SQLException {
        String query = "SELECT SCORE FROM HIGHSCORES WHERE NAME = ?";
        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setString(1, playerName);
            ResultSet results = statement.executeQuery();
            if (results.next()) {
                return results.getInt("SCORE");
            }
        }
        return null; 
    }

    public ArrayList<SnakeScore> getHighScores() throws SQLException {
        ArrayList<SnakeScore> highScores = new ArrayList<>();
        String query = "SELECT * FROM HIGHSCORES";
        Statement statement = conn.createStatement();
        ResultSet results ;
        try{
            results = statement.executeQuery(query);
        }catch(SQLException e){
            defaultStatement.execute();
            results = statement.executeQuery(query);
        }
        while (results.next()) {
            highScores.add(new SnakeScore(results.getString("NAME"), results.getInt("SCORE")));
        }
        sortByScore(highScores);
        return highScores;
    }
    
    private void updateScore(String name, int score) throws SQLException {
        String updateQuery = "UPDATE HIGHSCORES SET SCORE = ? WHERE NAME = ?";
        try (PreparedStatement statement = conn.prepareStatement(updateQuery)) {
            statement.setInt(1, score);
            statement.setString(2, name);
            statement.executeUpdate();
        }
    }
    
    private void insertScore(String name, int score) throws SQLException {
       try{
            insertStatement.setString(1, name);
            insertStatement.setInt(2, score);
            insertStatement.executeUpdate();
        } catch(SQLException e){
            defaultStatement.executeUpdate();
            insertStatement.setString(1, name);
            insertStatement.setInt(2, score);
            insertStatement.executeUpdate();
        }
    }
    
    public void putHighScore(String pName, int score) throws SQLException {
        Integer existingScore = getExistingScore(pName);

        if (existingScore != null) {
            if (score > existingScore) {
                // Update the score if the new score is higher than the existing score
                updateScore(pName, score);
            }
        } else {
            ArrayList<SnakeScore> currentHighScores = getHighScores();
            if (currentHighScores.size() < maxNumOfScores) {
                insertScore(pName, score);
            } else {
                int smallestScore = currentHighScores.get(currentHighScores.size() - 1).getScore();
                if (smallestScore < score) {
                    deleteScores(smallestScore);
                    insertScore(pName, score);
                }
            }
        }
    }

    private void sortByScore(ArrayList<SnakeScore> highScores){
        highScores.sort(SnakeScore::compareByScore);
    }

    private void deleteScores(int score) throws SQLException {
        deleteStatement.setInt(1, score);
        deleteStatement.executeUpdate();
    }
        
    public String[] getColumnNamesArray(){
        String[] columnNames = {"ID", "Name", "Score"};
        return columnNames;
    }
    
    public String[][] getDataTable() throws SQLException{
        ArrayList<SnakeScore> currentHighscores = getHighScores();
        String[][] data = new String[10][3];
        int ID = 1, counter=0;

        for(SnakeScore hs : currentHighscores){
            data[counter][0] = Integer.toString(ID++);
            data[counter][1] = hs.getName();
            data[counter][2] = Integer.toString(hs.getScore());
            counter++;
        }
        while(counter < 10){
            data[counter][0] = Integer.toString(ID++);
            data[counter][1] = "";
            data[counter][2] = "";
            counter++;
        }
        return data;
    }
}
