package model.statements;

import exceptions.MyException;
import model.adt.MyIDictionary;
import model.adt.MyIStack;
import model.state.PrgState;
import model.types.Type;

public class CompStmt implements IStmt {
    private IStmt first;
    private IStmt snd;

    public CompStmt(IStmt f, IStmt s) {
        first = f;
        snd = s;
    }

    @Override
    public String toString() {
        return "(" + first.toString() + "; " + snd.toString() + ")";
    }

    @Override
    public PrgState execute(PrgState state) throws MyException {
        MyIStack<IStmt> stk = state.getStk();
        stk.push(snd);
        stk.push(first);
        return null;
    }

    @Override
    public MyIDictionary<String, Type> typecheck(MyIDictionary<String, Type> typeEnv) throws MyException {
        return snd.typecheck(first.typecheck(typeEnv));
    }
}