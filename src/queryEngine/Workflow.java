package queryEngine;

import Exceptions.ParsingTransformedFileFailedException;
import Exceptions.TransfomationFailedException;
import Exceptions.WebServiceCallFailedException;
import dataModel.Table;

import java.util.*;

public class Workflow {


    private ArrayList<FunctionCall> functionCalls;
    private final Table ouputTable;

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

    public boolean isExecutable(){
        // checks if all function calls are executable
        // equivalent to check if all webServices are present

        for(FunctionCall f: functionCalls)
            if(! f.isExecutable())
                return false;

        return true;
    }

    public Table execute() throws ParsingTransformedFileFailedException,
                                  TransfomationFailedException,
                                  WebServiceCallFailedException {
        // execute the workflow
        FunctionCall f1 = functionCalls.get(0);

        Table t = f1.execute(f1.getConstants());
        // the remaining calls

        // do selection, if any required by the ws
        f1.filterTable(t);

        int i = 1;
        while (i < functionCalls.size()){

            // get input for this function call using the previous table
            // for every input, make a function call
            // make the union of all function calls
            // join the resulting function call with the previous table
            // make that table the new one for subsequential calls

            FunctionCall f = functionCalls.get(i);
            ArrayList<String[]> inputs = f.getInputFromTable(t);
            Table intermediateT = new Table(f.getSchema());

            for(String[] input: inputs)
                intermediateT.unionAll(f.execute(input));

            f.filterTable(intermediateT);

            //join the two tables
            t.join(intermediateT);

            i++;
        }

        // t is the resulting table
        // project on the outputing table

        t.project(ouputTable.getSchema());
        t.setName(ouputTable.getName());

        return t;

    }


}
