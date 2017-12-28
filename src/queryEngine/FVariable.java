package queryEngine;

import java.util.Objects;

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
    public String getValue() {
        return null;
    }


    @Override
    public boolean isVariable() {
        return true;
    }

    @Override
    public boolean isConstant() {
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FVariable fVariable = (FVariable) o;

        return fVariable.getDirection() == getDirection() && fVariable.name.equals(name);
    }

    @Override
    public int hashCode() {

        return Objects.hash(name+getDirection());
    }
}
