package model.adt;

import exceptions.MyException;

import java.io.BufferedReader;
import java.util.Map;

public interface MyIFileTable {
    void put(String key, BufferedReader value);
    BufferedReader lookup(String key) throws MyException;
    boolean isDefined(String key);
    void remove(String key) throws MyException;
    String toString();
    Map<String, BufferedReader> getContent();
}
