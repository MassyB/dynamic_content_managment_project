package queryEngine;

public abstract class FunctionArgument {
    public enum Direction { input, output }

    protected Direction direction;


    public Direction getDirection() {
        return direction;
    }

    public abstract boolean isVariable();
    public abstract boolean isConstant();
}
