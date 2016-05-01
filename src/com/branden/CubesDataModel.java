package com.branden;

import javax.swing.table.AbstractTableModel;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CubesDataModel extends AbstractTableModel {
    private int rowCount;
    private int columnCount;
    ResultSet rs;


    CubesDataModel(ResultSet rs) {
        this.rs = rs;
        countRows();
        countColumns();
    }

    private void countRows() {
        rowCount = 0;
        try {
            // rest pointer to before first record
            rs.beforeFirst();

            while (rs.next()) {
                rowCount++;
            }
            // rest pointer to before first record
            rs.beforeFirst();
        } catch (SQLException ex) {
            System.out.println("Error with countRows" + ex);
        }

    }

    private void countColumns() {
        try {
            columnCount = rs.getMetaData().getColumnCount();
        } catch (SQLException ex) {
            System.out.println("Error with countColumns" + ex);
        }
    }

    @Override
    public int getRowCount() {
        return rowCount;
    }

    @Override
    // return column count minus 1. We have 3 columns, the last is the ID which doesn't need to be displayed.3
    public int getColumnCount() {
        return columnCount -1;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
            try {
                rs.absolute(rowIndex + 1);
                Object o = rs.getObject(columnIndex + 1);
                return o.toString();
            } catch (SQLException ex) {
                System.out.println("Error with the getValue Call" + ex);
            }
        return "Error";
    }
    @Override
    public boolean isCellEditable(int row,int column){
        // magic number
        return column == 1;
    }
    //https://docs.oracle.com/javase/7/docs/api/javax/swing/JTable.html#setValueAt(java.lang.Object,%20int,%20int)
    @Override
    public void setValueAt(Object newValue, int row, int column){
        double newTime;
        try{
           newTime = Double.parseDouble( newValue.toString() );
        } catch (NumberFormatException ex){
            System.out.println("Error with new time" + ex);
            return;
        }
        //This only happens if the new rating is valid
        // from example MovieDataModel
        try {
            rs.absolute(row + 1);
            int solveTimeColumn = rs.findColumn(DatabaseManagerForm.SOLVER_TIME_COLUMN);
            rs.updateDouble(solveTimeColumn, newTime);
            rs.updateRow();
            //countRows();
            fireTableDataChanged();
        } catch (SQLException e) {
            System.out.println("Error updating time " + e);
        }
    }
    public void insertRow( String name, Double time){
        System.out.println("Inserting row\n");
        try {
            rs.moveToInsertRow();
            rs.updateDouble(DatabaseManagerForm.SOLVER_TIME_COLUMN, time);
            rs.updateString(DatabaseManagerForm.SOLVER_NAME_COLUMN, name);
            rs.insertRow();
            rs.moveToCurrentRow();
            //https://docs.oracle.com/javase/7/docs/api/javax/swing/table/AbstractTableModel.html#fireTableDataChanged()
            countRows();
            fireTableDataChanged();
        } catch (SQLException ex){
            System.out.println("Error with insert row " + ex);
        }
    }
    public void remove(int row){
       // move pointer to row to delete
        try {
            rs.absolute(row + 1);
            rs.deleteRow();
            countRows();
            fireTableDataChanged();
        } catch ( SQLException ex){
            System.out.println("Error deleting row" + ex);
        }
    }
}
