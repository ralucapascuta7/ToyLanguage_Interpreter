package model.adt;

import exceptions.MyException;
import javafx.util.Pair;
import model.statements.IStmt;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MySemaphoreTable implements MyISemaphoreTable {
    private Map<Integer, Pair<Integer, List<Integer>>> semaphoreTable;
    private int nextFreeLocation;
    private final Lock lock = new ReentrantLock();

    public MySemaphoreTable() {
        semaphoreTable = new HashMap<>();
        nextFreeLocation = 1;
    }

    @Override
    public int allocate(int value) {
        lock.lock();
        try {
            int location = nextFreeLocation;
            semaphoreTable.put(location, new Pair<>(value, new ArrayList<>()));
            nextFreeLocation++;
            return location;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Pair<Integer, List<Integer>> lookup(int location) throws MyException {
        lock.lock();
        try {
            if (!semaphoreTable.containsKey(location))
                throw new MyException("Semaphore location " + location + " not defined");
            return semaphoreTable.get(location);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void update(int location, Pair<Integer, List<Integer>> value) throws MyException {
        lock.lock();
        try {
            if (!semaphoreTable.containsKey(location))
                throw new MyException("Semaphore location " + location + " not defined");
            semaphoreTable.put(location, value);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean isDefined(int location) {
        lock.lock();
        try {
            return semaphoreTable.containsKey(location);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Map<Integer, Pair<Integer, List<Integer>>> getContent() {
        lock.lock();
        try {
            return semaphoreTable;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void setContent(Map<Integer, Pair<Integer, List<Integer>>> newContent) {
        lock.lock();
        try {
            semaphoreTable = new HashMap<>(newContent);
            int max = 0;
            for (int addr : semaphoreTable.keySet()) {
                if (addr > max) max = addr;
            }
            nextFreeLocation = max + 1;
        } finally {
            lock.unlock();
        }
    }

    public Lock getLock() {
        return lock;
    }

    @Override
    public String toString() {
        lock.lock();
        try {
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<Integer, Pair<Integer, List<Integer>>> e : semaphoreTable.entrySet()) {
                sb.append(e.getKey()).append(" -> (")
                        .append(e.getValue().getKey()).append(", ")
                        .append(e.getValue().getValue()).append(")\n");
            }
            return sb.toString();
        } finally {
            lock.unlock();
        }
    }
}