package wrappers;

import Exceptions.ParsingTransformedFileFailedException;
import Exceptions.TransfomationFailedException;
import Exceptions.WebServiceCallFailedException;
import dataModel.Table;
import download.WebService;
import parsers.ParseResultsForWS;
import parsers.WebServiceDescription;
import queryEngine.FunctionArgument;

import java.util.*;

public class WebServiceWrapper {

    private WebService webService;
    private String webServiceName;
    private Map<Integer, String> positionToArgName;
    private final String NODEF="NODEF";


    public WebServiceWrapper(String name){
        this.webServiceName = name;
        webService = WebServiceDescription.loadDescription(webServiceName);

        if(webService == null) return;

        //transform the list of tuples to a table
        positionToArgName = new HashMap<>();

        for(Map.Entry<String, Integer> e: webService.headVariableToPosition.entrySet())
            positionToArgName.put(e.getValue(), e.getKey());
    }


    public Table getResultTable(String... inputs)
            throws WebServiceCallFailedException,
                   TransfomationFailedException,
                   ParsingTransformedFileFailedException {

        // get the result file either by issuing an http request or by retrieving it from the cache
        String callResultFile = webService.getCallResult(inputs);

        if(callResultFile == null) throw new WebServiceCallFailedException(webServiceName, inputs);

        // transform the file to a universal format
        String transformationResultFile;
        try {
             transformationResultFile = webService.getTransformationResult(callResultFile);
             if (transformationResultFile == null) throw new Exception();
        } catch (Exception e) {
            throw new TransfomationFailedException(webServiceName, callResultFile);
        }

        // get the file as a list of tuples
        ArrayList<String[]> tuples;
        try {
             tuples = ParseResultsForWS.showResults(transformationResultFile, webService);
             if(tuples == null) throw new Exception();
        } catch (Exception e) {
             throw new ParsingTransformedFileFailedException(webServiceName, transformationResultFile);
        }

        Map<String, List<String>> rows = new HashMap<>();

        // init the rows hashMap
        for(String column: webService.headVariableToPosition.keySet())
            rows.put(column, new ArrayList<>());

        for(String[] tuple: tuples)
            for(int i=0; i < tuple.length; i++){
                // make the difference between the NODEF tuples and the others
                if(! tuple[i].equals(NODEF))
                    rows.get(positionToArgName.get(i)).add(tuple[i]);
                else
                    rows.get(positionToArgName.get(i)).add(inputs[i]);
            }


        return new Table(rows);
    }

    public Map<Integer, String> getPositionToArgName() {
        return positionToArgName;
    }

    public String getArgName(int i){
        return positionToArgName.get(i);
    }

    public Map<String, String> getRenamingMap(List<FunctionArgument> args){

        Map<String, String> oldToNewRenamingMap = new HashMap<String, String>();
        int i = 0;
        for(FunctionArgument arg: args){

            if(arg.isVariable())
                oldToNewRenamingMap.put(positionToArgName.get(i), arg.getName());

            i++;
        }
        return oldToNewRenamingMap;
    }
}
