package model.state;

import exceptions.MyException;
import model.adt.*;
import model.statements.IStmt;
import model.values.Value;

import java.io.BufferedReader;

public class PrgState {
    private MyIStack<IStmt> exeStack;
    private MyIDictionary<String, Value> symTable;
    private MyIList<Value> out;
    private MyIFileTable fileTable;
    private MyIHeap<Integer, Value> heap;
    private IStmt originalProgram;
    private MyISemaphoreTable semaphoreTable;
    private int id;

    private static int nextId = 1;

    public PrgState(MyIStack<IStmt> exeStack, MyIDictionary<String, Value> symTable, MyIList<Value> out, MyIDictionary<String, BufferedReader> fileTable, IStmt stmt) {
    }

    private static synchronized int getNextId() {
        return nextId++;
    }

    public PrgState(MyIStack<IStmt> stk,
                    MyIDictionary<String, Value> symtbl,
                    MyIList<Value> ot,
                    MyIFileTable ft,
                    MyIHeap<Integer, Value> hp,
                    MyISemaphoreTable st,
                    IStmt prg) {
        exeStack = stk;
        symTable = symtbl;
        out = ot;
        fileTable = ft;
        heap = hp;
        originalProgram = prg;
        id = getNextId();
        semaphoreTable = st;
        stk.push(prg);
    }

    public int getId() {
        return id;
    }

    public MyIStack<IStmt> getStk() {
        return exeStack;
    }

    public MyIDictionary<String, Value> getSymTable() {
        return symTable;
    }

    public MyIList<Value> getOut() {
        return out;
    }

    public MyIFileTable getFileTable() {
        return fileTable;
    }

    public MyIHeap<Integer, Value> getHeap() {
        return heap;
    }

    public MyISemaphoreTable getSemaphoreTable() {
        return semaphoreTable;
    }

    public IStmt getOriginalProgram() {
        return originalProgram;
    }

    public boolean isNotCompleted() {
        return !exeStack.isEmpty();
    }

    public PrgState oneStep() throws MyException {
        if (exeStack.isEmpty())
            throw new MyException("PrgState stack is empty");
        IStmt crtStmt = exeStack.pop();
        return crtStmt.execute(this);
    }

    @Override
    public String toString() {
        return "ID: " + id + "\n" +
                "ExeStack:\n" + exeStack.toString() +
                "SymTable:\n" + symTable.toString() +
                "Out:\n" + out.toString() +
                "Heap:\n" + heap.toString() +
                "SemaphoreTable:\n" + semaphoreTable.toString() +
                "FileTable:\n" + fileTable.toString() +
                "-----------------------------------\n";
    }
}