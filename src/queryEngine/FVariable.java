package queryEngine;

public class FVariable extends FunctionArgument{

    private String name;

    public FVariable(Direction direction, String name) {
        this.direction = direction;
        this.name = name;
    }

    public String getName(){
        return name;
    }


    @Override
    public boolean isVariable() {
        return true;
    }

    @Override
    public boolean isConstant() {
        return false;
    }
}
