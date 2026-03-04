package model.adt;

import exceptions.MyException;
import javafx.util.Pair;
import java.util.List;
import java.util.Map;

public interface MyISemaphoreTable {
    int allocate(int value);
    Pair<Integer, List<Integer>> lookup(int location) throws MyException;
    void update(int location, Pair<Integer, List<Integer>> value) throws MyException;
    boolean isDefined(int location);
    Map<Integer, Pair<Integer, List<Integer>>> getContent();
    void setContent(Map<Integer, Pair<Integer, List<Integer>>> newContent);
}