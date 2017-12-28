package queryEngine;

import java.util.ArrayList;

public class FunctionCall {

    private String name;
    private ArrayList<FunctionArgument> arguments;

    public FunctionCall(String name, ArrayList<FunctionArgument> arguments) {
        this.name = name;
        this.arguments = arguments;
    }

    public ArrayList<FunctionArgument> getArguments() {
        return arguments;
    }

    public String getName() {
        return name;
    }
}
