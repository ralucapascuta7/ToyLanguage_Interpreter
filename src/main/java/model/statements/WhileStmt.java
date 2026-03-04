package model.statements;

import exceptions.MyException;
import model.adt.MyIDictionary;
import model.adt.MyIHeap;
import model.adt.MyIStack;
import model.expressions.Exp;
import model.state.PrgState;
import model.types.BoolType;
import model.types.Type;
import model.values.BoolValue;
import model.values.Value;

public class WhileStmt implements IStmt {
    private Exp exp;
    private IStmt stmt;

    public WhileStmt(Exp exp, IStmt stmt) {
        this.exp = exp;
        this.stmt = stmt;
    }

    @Override
    public PrgState execute(PrgState state) throws MyException {
        MyIDictionary<String, Value> symTbl = state.getSymTable();
        MyIHeap<Integer, Value> heap = state.getHeap();
        MyIStack<IStmt> stk = state.getStk();

        Value cond = exp.eval(symTbl, heap);
        if (!cond.getType().equals(new BoolType()))
            throw new MyException("While: condition is not boolean");

        BoolValue b = (BoolValue) cond;
        if (b.getVal()) {
            stk.push(this);
            stk.push(stmt);
        }
        return null;
    }

    @Override
    public MyIDictionary<String, Type> typecheck(MyIDictionary<String, Type> typeEnv) throws MyException {
        Type typexp = exp.typecheck(typeEnv);
        if (typexp.equals(new BoolType())) {
            stmt.typecheck(typeEnv.deepCopy());
            return typeEnv;
        } else
            throw new MyException("The condition of WHILE has not the type bool");
    }

    @Override
    public String toString() {
        return "while(" + exp.toString() + ") " + stmt.toString();
    }
}