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
        int count = 0;
        try {
            while (rs.next()) {
                count++;
            }
        } catch (SQLException ex) {
            System.out.println("Error with countRows" + ex);
        }
        rowCount = count;
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
        System.out.println(rowIndex + " " + columnIndex);
            try {
                rs.absolute(rowIndex + 1);
                Object o = rs.getObject(columnIndex + 1);
                return o.toString();
            } catch (SQLException ex) {
                System.out.println("Error with the getValue Call" + ex);
            }
        return "Error";
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
            fireTableDataChanged();
        } catch (SQLException ex){
            System.out.println("Error with insert row " + ex);
        }
    }
}
