package queryEngine;

import dataModel.Table;

import java.util.ArrayList;

public class Workflow {


    private ArrayList<FunctionCall> functionCalls;
    private Table ouputTable;

    public Workflow(ArrayList<FunctionCall> functionCalls, Table ouputTable) {
        this.functionCalls = functionCalls;
        this.ouputTable = ouputTable;
    }


}
