package model.statements;

import exceptions.MyException;
import model.adt.MyIDictionary;
import model.adt.MyIList;
import model.expressions.Exp;
import model.state.PrgState;
import model.types.Type;
import model.values.Value;

public class PrintStmt implements IStmt {
    private Exp exp;

    public PrintStmt(Exp e) {
        exp = e;
    }

    @Override
    public String toString() {
        return "print(" + exp.toString() + ")";
    }

    @Override
    public PrgState execute(PrgState state) throws MyException {
        MyIList<Value> out = state.getOut();
        MyIDictionary<String, Value> symTbl = state.getSymTable();
        Value val = exp.eval(symTbl, state.getHeap());
        out.add(val);
        return null;
    }

    @Override
    public MyIDictionary<String, Type> typecheck(MyIDictionary<String, Type> typeEnv) throws MyException {
        exp.typecheck(typeEnv);
        return typeEnv;
    }
}