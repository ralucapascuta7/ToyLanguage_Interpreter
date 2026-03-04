package model.statements;

import exceptions.MyException;
import model.adt.MyIDictionary;
import model.state.PrgState;
import model.types.Type;

public class NopStmt implements IStmt {
    @Override
    public String toString() {
        return "nop";
    }

    @Override
    public PrgState execute(PrgState state) throws MyException {
        return null;
    }

    @Override
    public MyIDictionary<String, Type> typecheck(MyIDictionary<String, Type> typeEnv) throws MyException {
        return typeEnv;
    }
}