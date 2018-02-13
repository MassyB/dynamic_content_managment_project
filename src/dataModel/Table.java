package dataModel;

import java.util.*;

/**
 *  class modeling a table, where rows is a map that has column as key and all the cells (list) as value.
 *  Name is just name of the table
 *
 * */

public class Table {

    private Map<String,List<String>> rows;
    private String name;

    /*
    *  constructors
    * */

    /**
     *  constructor which creates a table from a map of columns
     * */
    public Table(Map<String, List<String>> rows) {
        this.rows = rows;
        removeDuplicates();
    }

    /**
     *  constructor which creates an empty table
     * */

    public Table(Set<String> columns){

        rows = new HashMap<>();

        for(String column: columns){
            rows.put(column, new ArrayList<>());
        }
    }
    /**
     *  constructor creates an empty table and give it a name
     * */
    public Table(Set<String> columns, String name){
        this(columns);
        this.name = name;
    }

    /*
    *  Getters and setters
    * */

    /**
     * return the name of each column
     * */
    public Set<String> getSchema(){

        return rows.keySet();
    }

    /**
     *  returns the number of rows in the table
     * */

    public int getSize(){

        Iterator<String> columnIter =  rows.keySet().iterator();
        if(columnIter.hasNext()){

            // size of one of the columns, equivalent to the size of the table
            return rows.get(columnIter.next()).size();

        }
        // empty table : no columns and no rows
        return 0;

    }

    /**
     *  @param columns name of the columns to return a view on
     *  @return a view on a bunch of selected columns
     * */

    public Map<String, List<String>> getColumns(List<String> columns){

        Map<String, List<String>> view = new HashMap<String, List<String>>();
        for(String column: columns)
            view.put(column, rows.get(column));

        return view;

    }

    /**
     *  return the column of the table
     * */
    public List<String> getColumn(String column){
        return rows.get(column);
    }

    /**
     *  get a new copy of the table
     * */
    public Table getCopy(){
        return new Table(new HashMap<String,List<String>>(rows));
    }

    /**
     *  get the name field of the table
     * */
    public String getName() {
        return name;
    }

    /**
     *  set the name of the table
     * */
    public void setName(String name) {
        this.name = name;
    }

    /**
     *  remove duplicate rows from the table
     * */

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


    /*
    *  table operations
    *  all the operations induce change of the current object (inplace operations)
    * */

    /**
     *  delete a row form the table
     *  @param idx index of the row to delete
     * */
    private void deleteRow(int idx){

        for(String column: rows.keySet()){
            rows.get(column).remove(idx);
        }
    }
    /**
     *  return a row
     *  @param idx index of the row to return
     *  @return a map where the key is the column name and the value is the string contained in the cell
     **/
    private Map<String, String> getRow(int idx){
        Map<String, String> row = new HashMap<>();
        for(String column: rows.keySet()){
            row.put(column, rows.get(column).get(idx));
        }
        return row;
    }

    /**
     *  add a row to a table
     *  @param row map representing a row each value is indexed by the name of the column (map key)
     * */
    private void addRow(Map<String, String> row){

        for(String column: row.keySet())
            rows.get(column).add(row.get(column));
    }

    /**
     *  @param criteria map keyed by the column name, and criteria.get(key) are the allowed values
     *  @return returns true iff row.get(k) equals at least one of criteria.get(key) for every key of criteria
     *
     * */

    private boolean rowHasAll(Map<String, String> row, Map<String, Set<String>>  criteria){
        // returns false if the row doesn't comply to one of the criteria
        // a criterion is modeled as a set of allowed values for a specific column

        for(String criteriaColumn: criteria.keySet()){
            if(! criteria.get(criteriaColumn).contains(row.get(criteriaColumn)) )
                return false;
        }

        return true;
    }

    /**
     * @param columns columns to project on
     * projection of the table on a set of columns. The table is modified inplace
     * */
    public void project(Set<String> columns){

        //if arguments in the right hand side are not present in the left hand side
        // the projection will be empty

        Set<String> tableColumns = new HashSet<>(rows.keySet());
        // columns to discard
        tableColumns.removeAll(columns);

        for(String column: tableColumns){
            rows.remove(column);
        }

        removeDuplicates();
    }

    /** @param criteria map keyed by the column name, and criteria.get(key) are the allowed values
     *  keep only rows of the table that satisfy the criteria
     *
     * */

    public void select(Map<String, Set<String>> criteria){

        if (criteria.isEmpty()) return;

        Table copy = new Table(getSchema());
        for(int i=0; i < getSize(); i ++){

            Map<String, String> row = getRow(i);
            if (rowHasAll(row, criteria)){
                copy.addRow(row);
            }
        }

        rows = copy.rows;
    }

    /**
     *  @param column name of the column to test
     *  @param value  the value allowed for the column
     *  keep only rows that have column value eqaul to the argument "value"
     * */

    public void select(String column, String value){

        Map<String, Set<String>> map = new HashMap<String, Set<String>>();
        Set<String> set = new HashSet<String>();
        set.add(value);
        map.put(column, set);

        select(map);
    }

    /**
     * @param row1 map representing row to join
     * @param row2 map representing the row to join with
     * @param columnsToJoinOn names of columns to join on
     *
     * @return true iff row1.get(k) == row2.get(k) for every k in columnsToJoinOn
     * see's if two row are joinable, join attributes are given by columnToJoinOn set
     *
     */

    private boolean rowsAreJoinable(Map<String, String> row1, Map<String, String> row2,
                                    Set<String> columnsToJoinOn){

        for(String joinColumn: columnsToJoinOn){
            if (! row1.get(joinColumn).equals(row2.get(joinColumn)) )
                return false;
        }
        return true;
    }

    /**
     * @param row1 map representing row to join
     * @param row2 map representing the row to join with
     * @param columnsToJoinOn names of columns to join on
     *
     * @return returns the result of the join between row1 and row2
     *
     * joins between row1 and row2, common columns are appear only once
     *
     * */

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

    /**
     * @param table the table to join with
     * performs the join between this table and the table in the argument.
     * The join algorithm used is the nested loops.
     * This table is modified.
     *
     * */

    public void join(Table table){
        // using the nested loops algorithm
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

    /**
     * @param t table to union with
     * performs the union between this table and table t. duplicates are kept. This table is modified
     *
     * */

    public void unionAll(Table t){
        // the two tables have the same schema
        // this modify the table, as the other operations do

        for(String column: t.rows.keySet())
            rows.get(column).addAll(t.rows.get(column));

    }

    /**
     *  @param oldToNew map containing the new names, keys are the old names and values are the new ones.
     *  renames the schema of the table.
     * */

    public void rename(Map<String, String> oldToNew){
        // rename the columns of a table
        // changes are made inplace
        for(String column: oldToNew.keySet())
            rows.put( oldToNew.get(column), rows.remove(column) );
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
