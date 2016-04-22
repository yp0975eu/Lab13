package com.branden;


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
    private  Statement st = null;
    private PreparedStatement preparedSelectByName = null;
    private PreparedStatement preparedInsert = null;
    private PreparedStatement preparedUpdateTime = null;

    private  Scanner scanner = new Scanner( System.in );

    private int userSelection; // used to control main program loop
    private boolean addAnother = true; // control main program loop
    private HashMap<String, Double> seedData = new HashMap<>(); // used for storing seed data

    public void run(){
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

             st = connection.createStatement();
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
         // always true, will exit loop when user selects quit from menu
         while( addAnother ) {
             userSelection = getUserSelection();

             switch (userSelection) {
                 case 1: {
                     getSolverInfo();
                     break;
                 }
                 case 2: {
                     showTable();
                     updateTimeEntry();
                     break;
                 }
                 case 3: {
                     showTable();
                     break;
                 }
                 case 4: {
                     //exit loop and //return to main and exit
                     System.out.println("Goodbye");
                     addAnother = false;
                     break;
                 }
                 default: {
                     //exit return to main and exit;
                     return;
                 }
             }
         }
         //return to main and exit
         return;
    }
    // get user selection
    private int getUserSelection(){
        printMenu();
        while ( !scanner.hasNextInt() ){
            System.out.println("Please enter a valid selection");
            scanner.next();
        }
        return scanner.nextInt();
    }
    // print options menu
    private void printMenu(){
        System.out.println("Enter your selection.");
        System.out.println("1. Add New Solver");
        System.out.println("2. Update Solver");
        System.out.println("3. Show Table");
        System.out.println("4. Quit");
    }
    // get info for adding new database entry
    private void getSolverInfo(){
        String name = getStringWithScanner("Enter name of solver");
        double solveTime = getDoubleWithScanner("Enter solve time");
        try {
            addToDatabase(name, solveTime);
        } catch (SQLException exception){
            System.out.println("Error adding solver info to the database." + exception);
        }
    }
    private void addToDatabase(String name, double solveTime) throws SQLException{
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
    private String getStringWithScanner(String prompt){
        System.out.println(prompt);
        String input = scanner.next();
        return input;
    }
    private double getDoubleWithScanner(String prompt) {
        System.out.println(prompt);
        while (!scanner.hasNextDouble()) {
            System.out.println("Please enter the solve time");
        }
        return scanner.nextDouble();
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
    private void showTable(){
        ResultSet rs = null;
        System.out.println("Name : Time");
        try {
            rs = st.executeQuery("SELECT * FROM Cubes");
            printResults( rs );
            System.out.println("");
        }catch (SQLException exc){
            System.out.println("Problem with selecting from database"+ exc);
        }
    }
    private void updateTimeEntry(){
        Boolean isThisRight = false;
        ResultSet rs;
        String name = null;
        String userInput;
        try {
            while( !isThisRight ){
                name = getStringWithScanner("Enter name to update");
                rs = searchByName(name);
                printResults(rs);
                userInput = getStringWithScanner("Is this the right record to update? Y or N or enter Q to quit");
                if (userInput.equalsIgnoreCase("y")){
                    isThisRight = true;
                } else if ( userInput.equalsIgnoreCase("q")){
                    // return to main loop
                    return;
                }
            }
            Double newTime = getDoubleWithScanner("Enter new time");
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
