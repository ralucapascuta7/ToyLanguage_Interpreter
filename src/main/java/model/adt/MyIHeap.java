package model.adt;

import exceptions.MyException;

import java.util.Map;

public interface MyIHeap<K, V> {
    int allocate(V value);
    void update(int address, V value) throws MyException;
    V lookup(int address) throws MyException;
    boolean isDefined(int address);
    void deallocate(int address) throws MyException;
    Map<Integer, V> getContent();
    void setContent(Map<Integer, V> newContent);
    String toString();
}