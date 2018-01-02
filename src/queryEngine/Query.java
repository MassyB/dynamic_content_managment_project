package queryEngine;

import dataModel.Table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Query {
    // see if a query is syntactically correct
    // example of a query :
    // output(?arg1,?arg2)<-webService1^ioo("cst",?param1,?param2)#webService2^io(?param1,?param2,?p)
    private String identifierStr = "[a-zA-Z0-9_]+";
    private String parameterStr = "\\?"+identifierStr;
    private String constantStr = '"'+".+"+'"';
    private String argumentStr= "("+constantStr+"|"+parameterStr+")";
    private String directionStr = "[io]+";
    private String functionCallSeparatorStr = "#";
    private String directionIndicatorStr = "\\^";
    private String arrowStr = "<-";

    private String queryRegex = identifierStr+ // output
                                "\\("+ "("+parameterStr+",)*" + parameterStr + "\\)"+ // (?arg1,?arg2)
                                arrowStr+ // <-
                                "("+
                                identifierStr+ //webService1
                                directionIndicatorStr+ //^
                                directionStr+ //ioooo
                                "\\("+ "("+argumentStr+",)*" + argumentStr + "\\)"+ // (?arg1,?arg2)
                                functionCallSeparatorStr+")*"+ //#
                                identifierStr+ //webService1
                                directionIndicatorStr+ //^
                                directionStr+ //ioooo
                                "\\("+ "("+argumentStr+",)*" + argumentStr + "\\)"; // (?arg1,?arg2)

    private Pattern queryPattern;
    private Pattern argumentPattern;
    private Pattern parameterPattern;

    private String query;

    private Workflow workflow;


    private static Query instance;

    private Query(){

        queryPattern = Pattern.compile(queryRegex);
        argumentPattern = Pattern.compile(argumentStr);
        parameterPattern = Pattern.compile(parameterStr);
    }

    public static Query parse(String query){
        if(instance == null)
            instance = new Query();

        // whenever this method is called initialize for parsing
        instance.query = query;
        instance.workflow = null;
        return instance;
    }

    public boolean isWellFormed(){
        // see if the query "this.query" is well formed

        if(! queryPattern.matcher(query).matches()) return false;

        // construct the workflow on the fly

        Table outputTable = extractOutputTable();

        // the other thing to verifiy is that number of directions is equal to number of arguments
        // for each function call

        ArrayList<FunctionCall> functionCalls = new ArrayList<>();
        String[] functionCallStrings = query.split(arrowStr)[1].split(functionCallSeparatorStr);
        String functionName;
        ArrayList<String> arguments;
        ArrayList<String> directions;


        for(String functionCall: functionCallStrings){

            functionName = extractFunctionName(functionCall);
            arguments = extractFunctionArguments(functionCall);

            //return null if the inputs are not declared first
            directions = extractFunctionArgumentDirections(functionCall);

            if (directions == null) return false;

            if(arguments.size() != directions.size()) return false;

            ArrayList<FunctionArgument> functionArguments = new ArrayList<>();
            FunctionArgument functionArgument;

            for(int i = 0; i < arguments.size(); i++){

                String s;
                if(isVariable(arguments.get(i))){
                    // a variable

                    s = arguments.get(i).substring(1);
                    functionArgument = new FVariable(getDirection(directions.get(i)), s);
                }else{
                    // a constant

                    s = arguments.get(i).substring(1, arguments.get(i).length()-1);
                    functionArgument = new FConstant(getDirection(directions.get(i)), s);
                }

                functionArguments.add(functionArgument);
            }

            functionCalls.add(new FunctionCall(functionName, functionArguments));
        }

        this.workflow = new Workflow(functionCalls, outputTable);

        return true;

    }

    public Workflow getWorkflow() {
        return workflow;
    }

    private String extractFunctionName(String functionCall){

        return functionCall.substring(0, functionCall.indexOf("^"));
    }

    private FunctionArgument.Direction getDirection(String s){
        if (s.equals("i"))
            return FunctionArgument.Direction.input;
        else if( s.equals("o"))
            return FunctionArgument.Direction.output;
        else
            return null;
    }


    private ArrayList<String> extractFunctionArguments(String functionCall){

        ArrayList<String> arguments = new ArrayList<>();

        Matcher m = argumentPattern.matcher(functionCall);
        String currentArg;

        while (m.find()){
            // + 1 to discard the "?": marker of an argument
            currentArg = functionCall.substring(m.start(), m.end());
            arguments.add(currentArg);
        }
        return arguments;
    }

    private ArrayList<String> extractFunctionArgumentDirections(String functionCall){

        String[] directions;

        directions  = functionCall.substring(functionCall.indexOf("^")+1 , functionCall.indexOf("("))
                                  .split("");

        //see if the inputs are declared first, if not return null
        if (String.join("",directions).matches("oi"))
                return null;

        return new ArrayList<>(Arrays.asList(directions));

    }

    private boolean isVariable(String argument){

        return argument.charAt(0) == '?';
        // otherwise it's a constant
    }



    private Table extractOutputTable(){


        String outputDefinition = query.split(arrowStr)[0];
        ArrayList<String> arguments = extractOutputArgument(outputDefinition);
        String outputName = extractOutputName(outputDefinition);

        Table outputTable = new Table(new HashSet<>(arguments), outputName);

        return outputTable;
    }

    private ArrayList<String> extractOutputArgument(String definition){

        ArrayList<String> arguments = new ArrayList<>();

        Matcher m = parameterPattern.matcher(definition);
        String currentArg;

        while (m.find()){
            // + 1 to discard the "?": marker of an argument
            currentArg = definition.substring(m.start() + 1 , m.end());
            arguments.add(currentArg);
        }
        return arguments;
    }

    private String extractOutputName(String outputDefinition){

        return outputDefinition.substring(0, outputDefinition.indexOf('('));
    }





}
