package queryEngine;

/**
 * class to model a function argument in the service call
 *
 * */

public abstract class FunctionArgument {
    public enum Direction { input, output }

    protected Direction direction;


    /**
     * can be in or out
     * */
    public Direction getDirection() {
        return direction;
    }


    public boolean isOutput(){

        return direction == Direction.output;
    }

    public boolean isInput(){
        return direction == Direction.input;
    }


    public abstract boolean isVariable();
    public abstract boolean isConstant();
    /**
     * only available for variable argument
     * */
    public abstract String getName();
    /**
     * only available for constant argument, "Frank sinatra"
     * */
    public abstract String getValue();
}
