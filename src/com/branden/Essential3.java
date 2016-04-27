package com.branden;


import javax.xml.transform.Result;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 Write an application that creates a table called “cubes”.

 You MUST start from scratch, not copy and paste from the examples!

 The cubes table should have two columns, one for the name of a thing that can solve Rubik’s cubes, and the other for the best time taken to solve the Rubik’s cube. Here’s some data:

 Sources: http://www.recordholders.org/en/list/rubik.html
 http://en.wikipedia.org/wiki/CubeStormer_II

 Your program should create this table, and add the data in the table above.

 Your column names should be all one word (such as cube_solver) and avoid SQL keywords in column names (so  avoid names like time)

 But best times can be improved, and new records might be added.

 Your program should be able to take input from the user when a new time should be recorded.  For example, your program should be able to ask if you want to add a new solver and time, for example,

 Cubestormer III robot
 3.253

 You can write this all in one class if you like.

 Please paste your Java code here:

 (Highly optional non-programming task, if you don’t have a time to add to the list, this might help http://ruwix.com/online-rubiks-cube-solver-program/)
 */
public class Essential3 {

    private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";        //Configure the driver needed
    private static final String CONNECTION_URL = "jdbc:mysql://localhost:3306/cubes";     //Connection string – where's the database?
    private static final String USER = "testuser"; // global user name
    private static final String PASSWORD = "password"; // global password
    private static Connection connection = null; // global connection
    private Statement st = null;
    private PreparedStatement preparedSelectByName = null;
    private PreparedStatement preparedInsert = null;
    private PreparedStatement preparedUpdateTime = null;

    private HashMap<String, Double> seedData = new HashMap<>(); // used for storing seed data

    // Renamed to fix constructor typo
    Essential3(){
        try {
            Class.forName(JDBC_DRIVER);
        } catch (ClassNotFoundException cnf){
            System.out.println("Error with database driver" + cnf);
            // if error return to main
            return;
        }

        try{
            connection = DriverManager.getConnection(CONNECTION_URL, USER, PASSWORD);
            preparedSelectByName = connection.prepareStatement("SELECT * FROM Cubes WHERE Solver Like ?");
            preparedInsert = connection.prepareStatement("INSERT into Cubes (Solver , SolveTime) VALUES (?,?)");
            preparedUpdateTime= connection.prepareStatement("UPDATE Cubes SET SolveTime = ? WHERE Solver Like ?");

        } catch (SQLException err){
            System.out.println("Error with connection  :" + err);
        }
         try{

             st = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
             //https://dev.mysql.com/doc/refman/5.5/en/create-table.html
             st.execute("CREATE TABLE IF NOT EXISTS Cubes (Solver VARCHAR(60), SolveTime FLOAT(3)) ");
         } catch (SQLException err){
             System.out.println("Error with statement  :" + err);
             // if error return to main
             return;
         }
         // seed database and catch any exceptions
         try{
            seedDatabase();
         }catch (SQLException exception){
             System.out.println("Error seeding database :\n"+exception );
         }
    }

    public void addToDatabase(String name, double solveTime) throws SQLException{
        // set name and solve time
        preparedInsert.setString(1, name);
        preparedInsert.setDouble(2, solveTime);
        preparedInsert.execute();

    }
    private ResultSet searchByName(String name)throws SQLException{
        // https://docs.oracle.com/javase/7/docs/api/java/sql/ResultSet.html
        // Initially the cursor is positioned before the first row.
        // The next method moves the cursor to the next row,
        // and because it returns false when there are no more rows in the ResultSet object,
        // it can be used in a while loop to iterate through the result set.
        preparedSelectByName.setString(1, "%"+name+"%");
        return preparedSelectByName.executeQuery();
    }

    private void seedDatabase() throws SQLException{
        seedData.put("Cubestormer II robot",5.270);
        seedData.put("Fakhri Raihaan (using his feet)",27.93);
        seedData.put("Ruxin Liu (age 3)",99.33);
        seedData.put("Mats Valk (human record holder)",6.27);

        // https://docs.oracle.com/javase/tutorial/jdbc/basics/prepared.html
        for (Map.Entry<String, Double> e : seedData.entrySet()) {
                // if there are no rows in the result set then add the name to the database
            if ( !searchByName(e.getKey()).next() ) {
                addToDatabase(e.getKey(), seedData.get(e.getKey()));
            }

        }
    }
    public ResultSet getAllFromCubes(){
        ResultSet rs = null;
        //System.out.println("Name : Time");
        try {
            rs = st.executeQuery("SELECT * FROM Cubes");
            //System.out.println("");
        }catch (SQLException exc){
            System.out.println("Problem with selecting from database"+ exc);
        }
        return rs;

    }
    private void updateTimeEntry( double newTime, String name){
       try{
            preparedUpdateTime.setDouble(1, newTime);
            preparedUpdateTime.setString(2, "%"+name+"%");
            preparedUpdateTime.executeUpdate();

        } catch (SQLException ex){
            System.out.println("Error updating time: "+ ex);
        }
    }
    private void printResults(ResultSet rs) throws SQLException{
        while (rs.next()) {
            System.out.printf("\t%s : %s \n", rs.getString("Solver"), rs.getString("SolveTime"));
        }
    }

}
