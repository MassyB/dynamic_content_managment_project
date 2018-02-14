package queryEngine;

import Exceptions.ParsingTransformedFileFailedException;
import Exceptions.TransfomationFailedException;
import Exceptions.WebServiceCallFailedException;
import dataModel.Table;
import wrappers.WebServiceWrapper;

import java.util.*;

/**
 * models a function call example: getAlbums(artistId)
 * */

public class FunctionCall {

    // name of the function call, example: getAlbums
    private String name;
    // arguments of the function [artistId]
    private ArrayList<FunctionArgument> arguments;
    // the actual object that's doing the http requests
    private WebServiceWrapper ws;

    public FunctionCall(String name, ArrayList<FunctionArgument> arguments) {
        this.name = name;
        this.arguments = arguments;
        ws = new WebServiceWrapper(name);
    }

    public boolean isExecutable(){

        if (! ws.isDefined())
            return false;

        // see if the underlying service has the same number of parameters as the function call
        if (arguments.size() != ws.getNumberOfParameters())
            return false;

        // see if the underlying service has all the required inputs/outputs
        for(int i=0; i < arguments.size(); i++){
            if(arguments.get(i).isInput())
                if (i > ws.getWsLastInputPostion())
                    return false;
        }

        return true;
    }
    /**
     * gets an array list of arguments
     * */
    public ArrayList<String[]> getInputFromTable(Table t){

        // capable of joining with the table
        Map<Integer, List<String>> posToValues = new HashMap<>();

        int i=0;
        boolean thereIsVariable = false;
        for(FunctionArgument arg: getInputArg()){

            if(arg.isVariable()){
                posToValues.put(i,t.getColumn(arg.getName()));
                thereIsVariable = true;
            }
            else{
                // arg is constant
                posToValues.put(i,getColumnVector(arg.getValue(), t.getSize()));
            }
            i++;
        }

        int tupleSize = getInputSize();
        ArrayList<String[]> inputs = new ArrayList<>();
        int inputSize = t.getSize();

        for(int j=0; j< inputSize; j++){
            String[] inputTuple = new String[tupleSize];

            for(Integer k: posToValues.keySet())
                inputTuple[k] = posToValues.get(k).get(j);

            inputs.add(inputTuple);
        }

        return inputs;
    }

    /***
     * @return returns a "length" sized list of s
     * example: getColumnVector('a',2) will return ['a', 'a']
     */
    public List<String> getColumnVector(String s, int lenght){

        List<String> columnVector = new ArrayList<>();

        for(int i=0; i < lenght; i++)
            columnVector.add(s);

        return columnVector;
    }


    public ArrayList<FunctionArgument> getInputArg(){

        ArrayList<FunctionArgument> inputArgs = new ArrayList<>();

        for(FunctionArgument arg: arguments)
            if(arg.isInput())
                inputArgs.add(arg);

        return inputArgs;
    }

    public int getInputSize(){
        return getInputArg().size();
    }

    public ArrayList<FunctionArgument> getArguments() {
        return arguments;
    }

    public String getName() {
        return name;
    }


    public String[] getConstants(){

        ArrayList<String> constants = new ArrayList<>();

        for(FunctionArgument arg: arguments){

            if(arg.isConstant())
                constants.add(arg.getValue());
        }
        String[] constantsArray = new String[constants.size()];

        for(int i=0; i < constants.size(); i ++)
            constantsArray[i] = constants.get(i);

        return constantsArray;
    }


    public Table execute(String... inputs)
            throws ParsingTransformedFileFailedException,
                   TransfomationFailedException,
                   WebServiceCallFailedException {


        // make the ws call
        Table t1 = ws.getResultTable(inputs);

        //rename the table such that it reflect the name given in the function call
        t1.rename(ws.getRenamingMap(arguments));

        return t1;
    }

    public Set<String> getSchema(){

        return new HashSet<String>(ws.getRenamingMap(arguments).values());
    }

    // table is filtered if necessary: arg t maybe modified
    public void filterTable(Table t){

        Map<String, Set<String>> criteria = new HashMap<>();

        int i = 0;
        for(FunctionArgument arg: arguments){

            if(arg.isConstant()){
                Set<String> s = new HashSet<>();
                s.add(arg.getValue());
                criteria.put(ws.getArgName(i),s );
            }
            i++;
        }

        t.select(criteria);
    }
}
