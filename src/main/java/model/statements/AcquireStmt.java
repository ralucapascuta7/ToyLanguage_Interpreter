package model.statements;

import exceptions.MyException;
import javafx.util.Pair;
import model.adt.MyIDictionary;
import model.adt.MyISemaphoreTable;
import model.adt.MySemaphoreTable;
import model.state.PrgState;
import model.types.IntType;
import model.types.Type;
import model.values.IntValue;
import model.values.Value;

import java.util.List;
import java.util.concurrent.locks.Lock;

public class AcquireStmt implements IStmt {
    private String varName;

    public AcquireStmt(String varName) {
        this.varName = varName;
    }

    @Override
    public PrgState execute(PrgState state) throws MyException {
        MyIDictionary<String, Value> symTable = state.getSymTable();
        MyISemaphoreTable semaphoreTable = state.getSemaphoreTable();

        if (!symTable.isDefined(varName))
            throw new MyException("acquire: variable " + varName + " not defined");

        Value val = symTable.lookup(varName);
        if (!(val instanceof IntValue))
            throw new MyException("acquire: variable must be int");

        int foundIndex = ((IntValue) val).getVal();


        Lock lock = ((MySemaphoreTable) semaphoreTable).getLock();
        lock.lock();
        try {
            if (!semaphoreTable.isDefined(foundIndex))
                throw new MyException("acquire: index not in SemaphoreTable");

            Pair<Integer, List<Integer>> entry = semaphoreTable.lookup(foundIndex);
            int N1 = entry.getKey();
            List<Integer> list1 = entry.getValue();
            int NL = list1.size();

            if (N1 > NL) {
                if (!list1.contains(state.getId())) {
                    list1.add(state.getId());
                    semaphoreTable.update(foundIndex, new Pair<>(N1, list1));
                }
            } else {
                state.getStk().push(this);
            }
        } finally {
            lock.unlock();
        }

        return null;
    }

    @Override
    public MyIDictionary<String, Type> typecheck(MyIDictionary<String, Type> typeEnv) throws MyException {
        Type varType = typeEnv.lookup(varName);
        if (!varType.equals(new IntType()))
            throw new MyException("acquire: variable must be int");
        return typeEnv;
    }

    @Override
    public String toString() {
        return "acquire(" + varName + ")";
    }
}