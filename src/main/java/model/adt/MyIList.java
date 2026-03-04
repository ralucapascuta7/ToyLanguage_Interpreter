package model.adt;

import java.util.List;

public interface MyIList<T> {
    void add(T elem);
    String toString();
    List<T> getContent();
}

