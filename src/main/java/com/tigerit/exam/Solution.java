package com.tigerit.exam;


import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * All of your application logic should be placed inside this class.
 * Remember we will load your application from our custom container.
 * You may add private method inside this class but, make sure your
 * application's execution points start from inside run method.
 */
public class Solution implements Runnable {
    private HashMap<String, DatabaseTable> tableShortName = new HashMap<>();
    private HashMap<String, DatabaseTable> tableList = new HashMap<>();
    private String[] query;

    @Override
    public void run() {
        int testCases = IO.readLineAsInteger();

        for (int testCase = 1; testCase <= testCases; testCase++) {
            collectDatabaseInfo(); // Table Input.
            IO.printLine("Test: " + testCase);
            processQuery(); // Query Processing.
        }
    }

    private void processQuery() {
        int totalQuery = IO.readLineAsInteger();

        for (int qry = 1; qry <= totalQuery; qry++) {
            String[] query = getQuery();

            ArrayList<String> table1Columns = new ArrayList<>();
            ArrayList<String> table2Columns = new ArrayList<>();

            DatabaseTable table1 = parseFrom(query[1]); //IO.printLine(table1.toString());
            DatabaseTable table2 = parseJoin(query[2]); //IO.printLine(table2.toString());
            int[] comparingColumns = parseCondition(query[3], table1); //IO.printLine(comparingColumns[0] + " " + comparingColumns[1]);
            parseSelect(query[0], table1, table2, table1Columns, table2Columns);

            DatabaseTable output = getJoinedResult(table1, table1Columns, table2, table2Columns, comparingColumns);
            //IO.printLine(table1.toString()); IO.printLine(table2.toString()); IO.printLine(output.toString());
            output.printResult();
        }
    }

    private DatabaseTable getJoinedResult(DatabaseTable t1, ArrayList<String> t1Columns,
                                           DatabaseTable t2, ArrayList<String> t2Columns, int[] compCols) {
        int t1CompCol = compCols[0];
        int t2CompCol = compCols[1];

        ArrayList<Integer> t1ColumnPos = new ArrayList<>();
        ArrayList<Integer> t2ColumnPos = new ArrayList<>();

        ArrayList<ArrayList<Integer> > data = new ArrayList<>();
        ArrayList<String> columnsNames = new ArrayList<>(t1Columns);
        columnsNames.addAll(t2Columns);
        HashMap<String, Integer> columnPos = new HashMap<>();

        for (String str : t1Columns) {
            t1ColumnPos.add(t1.columnPos.get(str));
        }

        for (String str : t2Columns) {
            t2ColumnPos.add(t2.columnPos.get(str));
        }

        for (int i = 0; i < t1.row; i++) {
            for (int j = 0; j < t2.row; j++) {
                ArrayList<Integer> rowData = new ArrayList<>();
                if (t1.tableData.get(i).get(t1CompCol) == t2.tableData.get(j).get(t2CompCol)) {
                    for (int pos = 0; pos < t1ColumnPos.size(); pos++) {
                        rowData.add(t1.tableData.get(i).get(t1ColumnPos.get(pos)));
                    }
                    for (int pos = 0; pos < t2ColumnPos.size(); pos++) {
                        rowData.add(t2.tableData.get(j).get(t2ColumnPos.get(pos)));
                    }
                    data.add(rowData);
                }
            }
        }

        return new DatabaseTable("Result", data.size(), columnsNames.size(), columnsNames, columnPos, data);
    }

    private void parseSelect(String s, DatabaseTable t1, DatabaseTable t2,
                                ArrayList<String> t1Columns, ArrayList<String> t2Columns) {
        String[] str = s.split("\\.|, | ");
        if (str[1].equals("*")) {
            t1Columns.addAll(t1.columnNames);
            t2Columns.addAll(t2.columnNames);
        }
        else {
            for (int i = 1; i < str.length; i+=2) {
                if (tableList.get(str[i]) == t1 || tableShortName.get(str[i]) == t1) {
                    t1Columns.add(str[i+1]);
                }
                else {
                    t2Columns.add(str[i+1]);
                }
            }
        }
    }

    private int[] parseCondition(String s, DatabaseTable t1) {
        int[] conditionColumn = new int[2];
        String[] str = s.split(" ");
        String leftOperand = str[1]; //IO.printLine(leftOperand);
        String rightOperand = str[3]; //IO.printLine(rightOperand);
        String[] leftPair = leftOperand.split("\\."); //IO.printLine(leftPair[0] + " " + leftPair[1]);
        String[] rightPair = rightOperand.split("\\."); //IO.printLine(rightPair[0] + " " + rightPair[1]);
        DatabaseTable leftTable = tableShortName.get(leftPair[0]) == null ? tableList.get(leftPair[0]) : tableShortName.get(leftPair[0]);
        DatabaseTable rightTable = tableShortName.get(rightPair[0]) == null ? tableList.get(rightPair[0]) : tableShortName.get(rightPair[0]);

        if (leftTable == t1) {
            conditionColumn[0] = leftTable.columnPos.get(leftPair[1]);
            conditionColumn[1] = rightTable.columnPos.get(rightPair[1]);
        }
        else {
            conditionColumn[1] = leftTable.columnPos.get(leftPair[1]);
            conditionColumn[0] = rightTable.columnPos.get(rightPair[1]);
        }

        return conditionColumn;
    }

    private DatabaseTable parseJoin(String s) {
        String[] str = s.split(" ");
        DatabaseTable tableLink = tableList.get(str[1]);
        if (str.length == 3) {
            tableShortName.put(str[2], tableLink);
        }
        return tableLink;
    }

    private DatabaseTable parseFrom(String s) {
        String[] str = s.split(" ");
        DatabaseTable tableLink = tableList.get(str[1]);
        if (str.length == 3) {
            tableShortName.put(str[2], tableLink);
        }
        return tableLink;
    }

    private void collectDatabaseInfo() {
        int totalTable = IO.readLineAsInteger();
        for (int table = 1; table <= totalTable; table++) {
            String tableName = IO.readLine();

            String[] dim = IO.readLine().split(" ", 2);
            int column = Integer.parseInt(dim[0]);
            int row = Integer.parseInt(dim[1]);

            String[] colNames = IO.readLine().split(" ", column);

            ArrayList<String> columnNames = new ArrayList<>();
            HashMap<String, Integer> columnPos = new HashMap<>();
            ArrayList<ArrayList<Integer>> tableData = new ArrayList<>();

            for (int c = 0; c < column; c++) {
                columnNames.add(colNames[c]);
                columnPos.put(colNames[c], c);
            }
            for (int r = 0; r < row; r++) {
                String[] val = IO.readLine().split(" ", column);
                ArrayList<Integer> list = new ArrayList<>();
                for (int c = 0; c < column; c++) {
                    list.add(Integer.parseInt(val[c]));
                }
                tableData.add(list);
            }
            tableList.put(tableName, new DatabaseTable(tableName, row, column, columnNames, columnPos, tableData));
            //IO.printLine(tableList.get(tableName).toString());
        }
    }

    public String[] getQuery() {
        String[] query = new String[5];

        for (int i = 0; i < 5; i++) {
            query[i] = IO.readLine();
        }

        return query;
    }
}
