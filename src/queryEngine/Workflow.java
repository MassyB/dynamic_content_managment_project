package queryEngine;

import dataModel.Table;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Workflow {


    private ArrayList<FunctionCall> functionCalls;
    private Table ouputTable;

    public Workflow(ArrayList<FunctionCall> functionCalls, Table ouputTable) {
        this.functionCalls = functionCalls;
        this.ouputTable = ouputTable;
    }

    public boolean isAdmissible(){

        Set<String> seenVars = new HashSet<>();

        for(FunctionCall functionCall: functionCalls){
            for(FunctionArgument arg : functionCall.getArguments()){

                if(arg.isVariable() && !seenVars.contains(arg.getName()) && arg.isInput() )
                    return false;
                if(arg.isVariable())
                    seenVars.add(arg.getName());
            }
        }
        return true;
    }

}
