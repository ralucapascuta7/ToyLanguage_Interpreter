package model.types;

import model.values.StringValue;
import model.values.Value;

public class StringType implements Type {
    public boolean equals(Object another) {
        return another instanceof StringType;
    }

    public String toString() {
        return "string";
    }

    public Value defaultValue() {
        return new StringValue("");
    }
}