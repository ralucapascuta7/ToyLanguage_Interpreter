package model.adt;

import exceptions.MyException;

import java.util.HashMap;
import java.util.Map;

public class MyDictionary<K, V> implements MyIDictionary<K, V> {
    private HashMap<K, V> dictionary;

    public MyDictionary() {
        dictionary = new HashMap<>();
    }

    @Override
    public void put(K key, V value) {
        dictionary.put(key, value);
    }

    @Override
    public V lookup(K key) throws MyException {
        if (!dictionary.containsKey(key))
            throw new MyException("Variable " + key + " is not defined in symbol table");
        return dictionary.get(key);
    }

    @Override
    public boolean isDefined(K key) {
        return dictionary.containsKey(key);
    }

    @Override
    public void update(K key, V value) throws MyException {
        if (!dictionary.containsKey(key))
            throw new MyException("Variable " + key + " is not defined in symbol table");
        dictionary.put(key, value);
    }

    @Override
    public Map<K, V> getContent() {
        return dictionary;
    }

    @Override
    @SuppressWarnings("unchecked")
    public MyIDictionary<K, V> deepCopy() {
        MyDictionary<K, V> newDict = new MyDictionary<>();
        for (Map.Entry<K, V> entry : dictionary.entrySet()) {
            K key = entry.getKey();
            V value = entry.getValue();
            if (value instanceof model.values.Value) {
                newDict.put(key, (V) ((model.values.Value) value).deepCopy());
            } else {
                newDict.put(key, value);
            }
        }
        return newDict;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<K, V> entry : dictionary.entrySet()) {
            sb.append(entry.getKey()).append(" --> ").append(entry.getValue()).append("\n");
        }
        return sb.toString();
    }
}