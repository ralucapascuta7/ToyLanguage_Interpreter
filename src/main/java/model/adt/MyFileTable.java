package model.adt;

import exceptions.MyException;

import java.io.BufferedReader;
import java.util.HashMap;
import java.util.Map;

public class MyFileTable implements MyIFileTable {
    private HashMap<String, BufferedReader> fileTable;

    public MyFileTable() {
        fileTable = new HashMap<>();
    }

    @Override
    public synchronized void put(String key, BufferedReader value) {
        fileTable.put(key, value);
    }

    @Override
    public synchronized BufferedReader lookup(String key) throws MyException {
        if (!fileTable.containsKey(key))
            throw new MyException("File " + key + " is not in the FileTable");
        return fileTable.get(key);
    }

    @Override
    public synchronized boolean isDefined(String key) {
        return fileTable.containsKey(key);
    }

    @Override
    public synchronized void remove(String key) throws MyException {
        if (!fileTable.containsKey(key))
            throw new MyException("File " + key + " is not in the FileTable");
        fileTable.remove(key);
    }

    @Override
    public synchronized Map<String, BufferedReader> getContent() {
        return fileTable;
    }

    @Override
    public synchronized String toString() {
        StringBuilder sb = new StringBuilder();
        for (String key : fileTable.keySet()) {
            sb.append(key).append("\n");
        }
        return sb.toString();
    }
}