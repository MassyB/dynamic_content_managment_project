package queryEngine;

import java.util.Objects;

/**
 * models the constant in the function call
 * */

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

    @Override
    public String getName() {
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FConstant fConstant = (FConstant) o;

        return getDirection() == fConstant.getDirection() && fConstant.value.equals(value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value+getDirection());
    }
}
