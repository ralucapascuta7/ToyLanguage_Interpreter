package model.values;

import model.types.Type;

public interface Value {
    Type getType();
    String toString();
    boolean equals(Object another);
    Value deepCopy();
}