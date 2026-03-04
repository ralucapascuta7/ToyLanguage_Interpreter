package model.statements;

import exceptions.MyException;
import javafx.util.Pair;
import model.adt.MyIDictionary;
import model.adt.MyISemaphoreTable;
import model.state.PrgState;
import model.types.IntType;
import model.types.Type;
import model.values.IntValue;
import model.values.Value;

import java.util.List;

public class ReleaseStmt implements IStmt {
    private String varName;

    public ReleaseStmt(String varName) {
        this.varName = varName;
    }

    @Override
    public PrgState execute(PrgState state) throws MyException {
        MyIDictionary<String, Value> symTable = state.getSymTable();
        MyISemaphoreTable semaphoreTable = state.getSemaphoreTable();

        if (!symTable.isDefined(varName))
            throw new MyException("release: variable " + varName + " not defined");

        Value val = symTable.lookup(varName);
        if (!(val instanceof IntValue))
            throw new MyException("release: variable must be int");

        int foundIndex = ((IntValue) val).getVal();

        synchronized (semaphoreTable) {
            if (!semaphoreTable.isDefined(foundIndex))
                throw new MyException("release: index not in SemaphoreTable");

            Pair<Integer, List<Integer>> entry = semaphoreTable.lookup(foundIndex);
            int N1 = entry.getKey();
            List<Integer> list1 = entry.getValue();

            if (list1.contains(state.getId())) {
                list1.remove(Integer.valueOf(state.getId()));
                semaphoreTable.update(foundIndex, new Pair<>(N1, list1));
            }
        }

        return null;
    }

    @Override
    public MyIDictionary<String, Type> typecheck(MyIDictionary<String, Type> typeEnv) throws MyException {
        Type varType = typeEnv.lookup(varName);
        if (!varType.equals(new IntType()))
            throw new MyException("release: variable must be int");
        return typeEnv;
    }

    @Override
    public String toString() {
        return "release(" + varName + ")";
    }
}