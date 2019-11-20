package com.tigerit.exam;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class DatabaseTable {
    public String tableName;
    public ArrayList<String> columnNames;
    public HashMap<String, Integer> columnPos;
    public ArrayList<ArrayList<Integer>> tableData;
    public int row, col;

    public DatabaseTable(String tableName, int row, int col, ArrayList<String> colNames,
                            HashMap<String, Integer> colPos, ArrayList<ArrayList<Integer>> data) {
        this.tableName = tableName;
        this.row = row;
        this.col = col;
        this.columnNames = colNames;
        this.columnPos = colPos;
        this.tableData = data;
    }

    public String toString() {
        String columns = "";
        String data = "";
        for (int i = 0; i < columnNames.size(); i++) {
            columns += columnNames.get(i);
            columns += " ";
        }

        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                data += Integer.toString(tableData.get(i).get(j));
                data += " ";
            }
            data += "\n";
        }

        return "Table Name: " + tableName + "\n" + columns + "\n" + row + " " + col + "\n" + data;
    }

    public void printResult() {
        for (String str : this.columnNames) {
            IO.print(str + " ");
        }
        IO.printLine("");

        Collections.sort(this.tableData, new Comparator<ArrayList<Integer>>() {
            @Override
            public int compare(ArrayList<Integer> o1, ArrayList<Integer> o2) {
                for (int i = 0; i < o1.size(); i++) {
                    if (o1.get(i) < o2.get(i)) {
                        return -1;
                    }
                    else if (o1.get(i) > o2.get(i)) {
                        return 1;
                    }
                }
                return 0;
            }
        });

        for (int i = 0; i < this.tableData.size(); i++) {
            for (int j = 0; j < this.tableData.get(i).size(); j++) {
                IO.print(this.tableData.get(i).get(j) + " ");
            }
            IO.printLine("");
        }
        IO.printLine("");
    }
}
