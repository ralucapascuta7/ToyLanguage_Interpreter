package model.values;

import model.types.IntType;
import model.types.Type;

public class IntValue implements Value {
    private int val;

    public IntValue(int v) {
        val = v;
    }

    public int getVal() {
        return val;
    }

    @Override
    public String toString() {
        return String.valueOf(val);
    }

    @Override
    public Type getType() {
        return new IntType();
    }

    @Override
    public boolean equals(Object another) {
        if (another instanceof IntValue)
            return ((IntValue) another).val == this.val;
        return false;
    }

    @Override
    public Value deepCopy() {
        return new IntValue(val);
    }
}