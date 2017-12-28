package queryEngine;

public abstract class FunctionArgument {
    public enum Direction { input, output }

    protected Direction direction;


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
    public abstract String getName();
    public abstract String getValue();
}
