package model.adt;

import exceptions.MyException;

import java.util.HashMap;
import java.util.Map;

public class MyHeap<V> implements MyIHeap<Integer, V> {
    private Map<Integer, V> heap;
    private int nextFreeAddress;

    public MyHeap() {
        heap = new HashMap<>();
        nextFreeAddress = 1;
    }

    @Override
    public synchronized int allocate(V value) {
        int addr = nextFreeAddress;
        heap.put(addr, value);
        nextFreeAddress++;
        return addr;
    }

    @Override
    public synchronized void update(int address, V value) throws MyException {
        if (!heap.containsKey(address))
            throw new MyException("Heap: address " + address + " not defined");
        heap.put(address, value);
    }

    @Override
    public synchronized V lookup(int address) throws MyException {
        if (!heap.containsKey(address))
            throw new MyException("Heap: address " + address + " not defined");
        return heap.get(address);
    }

    @Override
    public synchronized boolean isDefined(int address) {
        return heap.containsKey(address);
    }

    @Override
    public synchronized void deallocate(int address) throws MyException {
        if (!heap.containsKey(address))
            throw new MyException("Heap: address " + address + " not defined");
        heap.remove(address);
    }

    @Override
    public synchronized Map<Integer, V> getContent() {
        return heap;
    }

    @Override
    public synchronized void setContent(Map<Integer, V> newContent) {
        heap = new HashMap<>(newContent);
        int max = 0;
        for (int addr : heap.keySet()) {
            if (addr > max) max = addr;
        }
        nextFreeAddress = max + 1;
    }

    @Override
    public synchronized String toString() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<Integer, V> e : heap.entrySet()) {
            sb.append(e.getKey()).append(" -> ").append(e.getValue()).append("\n");
        }
        return sb.toString();
    }
}