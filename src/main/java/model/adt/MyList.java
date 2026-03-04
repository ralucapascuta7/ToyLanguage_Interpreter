package model.adt;

import java.util.ArrayList;
import java.util.List;

public class MyList<T> implements MyIList<T> {
    private ArrayList<T> list;

    public MyList() {
        list = new ArrayList<>();
    }

    @Override
    public synchronized void add(T elem) {
        list.add(elem);
    }

    @Override
    public List<T> getContent() {
        return list;
    }

    @Override
    public synchronized String toString() {
        StringBuilder sb = new StringBuilder();
        for (T elem : list) {
            sb.append(elem.toString()).append("\n");
        }
        return sb.toString();
    }
}