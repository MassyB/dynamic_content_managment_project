package dataModel;

import java.util.*;

public class Table {

    private Map<String,List<String>> rows;
    private String name;


    // constructors;
    public Table(Map<String, List<String>> rows) {
        this.rows = rows;
        removeDuplicates();
    }

    public Table(Set<String> columns){

        rows = new HashMap<>();

        for(String column: columns){
            rows.put(column, new ArrayList<>());
        }
    }

    public Table(Set<String> columns, String name){
        this(columns);
        this.name = name;
    }

    // useful methods
    public int getSize(){

        Iterator<String> columnIter =  rows.keySet().iterator();
        if(columnIter.hasNext()){

            // size of one fo the columns, equivalent to the size of the table
            return rows.get(columnIter.next()).size();

        }
        // empty table : no columns and no rows
        return 0;

    }

    public Table getCopy(){
        return new Table(new HashMap<String,List<String>>(rows));
    }

    private void removeDuplicates(){
        // eliminates duplicates in the current table
        HashSet<String> bagOfRows = new HashSet<>();

        Table table = new Table(new HashSet<>(rows.keySet()));

        for(int i=0; i < getSize(); i ++){

            String currentRow = getRow(i).toString();

            // if the row is new: not a duplicate
            if(! bagOfRows.contains(currentRow)){
                table.addRow(getRow(i));
                bagOfRows.add(currentRow);
            }
        }

        rows = table.rows;
    }

    // table operations
    // all the operations induce change of the current object (inplace operations)

    private void deleteRow(int idx){

        for(String column: rows.keySet()){
            rows.get(column).remove(idx);
        }
    }

    private Map<String, String> getRow(int idx){
        Map<String, String> row = new HashMap<>();
        for(String column: rows.keySet()){
            row.put(column, rows.get(column).get(idx));
        }
        return row;
    }

    private void addRow(Map<String, String> row){

        for(String column: row.keySet())
            rows.get(column).add(row.get(column));
    }

    private boolean rowHasAll(Map<String, String> row, Map<String, Set<String>>  criteria){
        // returns false if the row doesn't comply to one of the criterion
        // a criterion is modeled as a set of allowed values for a specific column

        for(String criteriaColumn: criteria.keySet()){
            if(! criteria.get(criteriaColumn).contains(row.get(criteriaColumn)) )
                return false;
        }

        return true;
    }


    public void project(Set<String> columns){

        Set<String> tableColumns = new HashSet<>(rows.keySet());
        // columns to discard
        tableColumns.removeAll(columns);

        for(String column: tableColumns){
            rows.remove(column);
        }

        removeDuplicates();
    }

    public void select(Map<String, Set<String>> criteria){

        Table copy = getCopy();

        for(int i=0; i < getSize(); i ++){

            Map<String, String> row = copy.getRow(i);
            if (! copy.rowHasAll(row, criteria))
                copy.deleteRow(i);

        }

        // updating the current table
        rows = copy.rows;
    }

    public void select(String column, String value){

        Map<String, Set<String>> map = new HashMap<String, Set<String>>();
        Set<String> set = new HashSet<String>();
        set.add(value);
        map.put(column, set);

        select(map);
    }

    private boolean rowsAreJoinable(Map<String, String> row1, Map<String, String> row2,
                                    Set<String> columnsToJoinOn){

        for(String joinColumn: columnsToJoinOn){
            if (! row1.get(joinColumn).equals(row2.get(joinColumn)) )
                return false;
        }
        return true;
    }

    private Map<String, String> joinRows(Map<String, String> row1, Map<String, String> row2,
                                         Set<String> columnsToJoinOn){

        // make the join of two joinable rows;

        Set<String> remainingColumns = new HashSet<>(row2.keySet());
        remainingColumns.removeAll(columnsToJoinOn);

        Map <String, String> joinedRow = new HashMap<>(row1);

        for(String column: remainingColumns){

            joinedRow.put(column, row2.get(column));
        }

        return joinedRow;
    }
    
    public void join(Table table){
        // using the nested loop algorithm
        // modify this table

        Table otherTable = table.getCopy();
        Table thisTable = getCopy();

        Set<String> columnsToJoinOn = new HashSet<>(otherTable.rows.keySet());
        columnsToJoinOn.retainAll(rows.keySet());
        if(columnsToJoinOn.isEmpty())
            // the empty table : no columns, no rows
            rows = new HashMap<>();

        // there is at least one column to join on

        Set<String> joinedColumns = new HashSet<>(rows.keySet());
        joinedColumns.addAll(otherTable.rows.keySet());

        Table joinedTable = new Table(joinedColumns);

        for(int i = 0; i < thisTable.getSize(); i ++){

            Map<String, String> thisRow = thisTable.getRow(i);

            for(int j = 0; j < otherTable.getSize(); j ++){

                Map<String, String> otherRow = otherTable.getRow(j);

                if(rowsAreJoinable(thisRow, otherRow, columnsToJoinOn)){

                    joinedTable.addRow(joinRows(thisRow, otherRow, columnsToJoinOn));
                }
                // do nothing : the rows are not joinable
            }
        }

        // update this table to reflect the result of the join

        rows = joinedTable.rows;
    }

    @Override
    public String toString() {
        StringBuilder tableString = new StringBuilder("");
        tableString.append(rows.keySet());

        for(int i = 0; i < getSize(); i ++){
            tableString.append("\n");
            tableString.append(getRow(i).values());
        }
        return tableString.toString();
    }
}
