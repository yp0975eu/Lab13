package com.branden;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.InputMismatchException;

public class DatabaseManagerForm extends JFrame{
    private JPanel rootPanel;
    private JTextField solverNameTextField;
    private JTextField solverTimeTextField;
    private JButton addButton;
    private JButton deleteSolverButton;
    private JTable solverTable;
    private JLabel timeErrorLabel;
    private Essential3 databaseApp;
    private CubesDataModel cubesDataModel;
    static public final String SOLVER_NAME_COLUMN = "Solver";
    static public final String SOLVER_TIME_COLUMN = "SolveTime";

    DatabaseManagerForm(Essential3 databaseApp){
        super("Solver Manger");
        this.databaseApp = databaseApp;
        setContentPane(rootPanel);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        cubesDataModel =  new CubesDataModel( this.databaseApp.getAllFromCubes());

        //JTable
        solverTable.setModel( cubesDataModel  );

        pack();
        setVisible(true);

        addButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e){
                boolean okayTime;
                boolean okayText;
                String name = solverNameTextField.getText();
                Double time = null;
                // clear any previous errors
                timeErrorLabel.setText("");

                okayText = name.length() > 0 ? true : false;

                try{
                    time = Double.parseDouble( solverTimeTextField.getText() );
                    okayTime = true;
                } catch (InputMismatchException err){
                    timeErrorLabel.setText("Please enter a valid time");
                    okayTime = false;
                    pack();
                }
                catch ( NumberFormatException err){
                    timeErrorLabel.setText("Please enter a valid time");
                    okayTime = false;
                    pack();
                }
                if ( okayText && okayTime ){

                        //databaseApp.addToDatabase(name, time);
                        // update model
                    cubesDataModel.insertRow(name, time);

                }
            }
        });
        deleteSolverButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int row = solverTable.getSelectedRow();
                // from movieForm example
                if (row != -1) {      // -1 means no row is selected.
                    cubesDataModel.remove(row);
                }

            }
        });
    }

}
