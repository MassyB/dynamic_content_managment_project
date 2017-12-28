package queryEngine;

public class FConstant extends FunctionArgument {

    private String value;

    public FConstant(Direction direction, String value) {
        this.direction = direction;
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean isVariable() {
        return false;
    }

    @Override
    public boolean isConstant() {
        return true;
    }
}
