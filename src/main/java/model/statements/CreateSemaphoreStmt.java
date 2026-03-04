package model.statements;

import exceptions.MyException;
import model.adt.MyIDictionary;
import model.expressions.Exp;
import model.state.PrgState;
import model.types.IntType;
import model.types.Type;
import model.values.IntValue;
import model.values.Value;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CreateSemaphoreStmt implements IStmt {
    private String varName;
    private Exp exp;
    private static final Lock lock = new ReentrantLock();

    public CreateSemaphoreStmt(String varName, Exp exp) {
        this.varName = varName;
        this.exp = exp;
    }

    @Override
    public PrgState execute(PrgState state) throws MyException {
        lock.lock();
        if (!state.getSymTable().isDefined(varName))
            throw new MyException("createSemaphore: variable " + varName + " not defined");

        if (!state.getSymTable().lookup(varName).getType().equals(new IntType()))
            throw new MyException("createSemaphore: variable must be int");

        Value val = exp.eval(state.getSymTable(), state.getHeap());
        if (!(val instanceof IntValue))
            throw new MyException("createSemaphore: expression must evaluate to int");

        int number1 = ((IntValue) val).getVal();
        int location = state.getSemaphoreTable().allocate(number1);

        state.getSymTable().update(varName, new IntValue(location));

        lock.unlock();
        return null;
    }

    @Override
    public MyIDictionary<String, Type> typecheck(MyIDictionary<String, Type> typeEnv) throws MyException {
        Type varType = typeEnv.lookup(varName);
        Type expType = exp.typecheck(typeEnv);

        if (!varType.equals(new IntType()))
            throw new MyException("createSemaphore: variable must be int");
        if (!expType.equals(new IntType()))
            throw new MyException("createSemaphore: expression must be int");

        return typeEnv;
    }

    @Override
    public String toString() {
        return "createSemaphore(" + varName + ", " + exp + ")";
    }
}