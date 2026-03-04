package model.values;

import model.types.BoolType;
import model.types.Type;

public class BoolValue implements Value {
    private boolean val;

    public BoolValue(boolean v) {
        val = v;
    }

    public boolean getVal() {
        return val;
    }

    @Override
    public String toString() {
        return String.valueOf(val);
    }

    @Override
    public Type getType() {
        return new BoolType();
    }

    @Override
    public boolean equals(Object another) {
        if (another instanceof BoolValue)
            return ((BoolValue) another).val == this.val;
        return false;
    }

    @Override
    public Value deepCopy() {
        return new BoolValue(val);
    }
}