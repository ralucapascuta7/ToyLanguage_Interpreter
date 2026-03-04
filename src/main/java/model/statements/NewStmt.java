package model.statements;

import exceptions.MyException;
import model.adt.MyIDictionary;
import model.adt.MyIHeap;
import model.expressions.Exp;
import model.state.PrgState;
import model.types.RefType;
import model.types.Type;
import model.values.RefValue;
import model.values.Value;

public class NewStmt implements IStmt {
    private String varName;
    private Exp exp;

    public NewStmt(String varName, Exp exp) {
        this.varName = varName;
        this.exp = exp;
    }

    @Override
    public PrgState execute(PrgState state) throws MyException {
        MyIDictionary<String, Value> symTbl = state.getSymTable();
        MyIHeap<Integer, Value> heap = state.getHeap();

        if (!symTbl.isDefined(varName))
            throw new MyException("new: variable " + varName + " is not defined");

        Value varVal = symTbl.lookup(varName);
        if (!(varVal.getType() instanceof RefType))
            throw new MyException("new: variable " + varName + " is not of RefType");

        RefType refType = (RefType) varVal.getType();
        Value evalVal = exp.eval(symTbl, heap);

        if (!evalVal.getType().equals(refType.getInner()))
            throw new MyException("new: types do not match: " + evalVal.getType() +
                    " vs " + refType.getInner());

        int newAddr = heap.allocate(evalVal);
        symTbl.update(varName, new RefValue(newAddr, refType.getInner()));
        return null;
    }

    @Override
    public MyIDictionary<String, Type> typecheck(MyIDictionary<String, Type> typeEnv) throws MyException {
        Type typevar = typeEnv.lookup(varName);
        Type typexp = exp.typecheck(typeEnv);
        if (typevar.equals(new RefType(typexp)))
            return typeEnv;
        else
            throw new MyException("NEW stmt: right hand side and left hand side have different types");
    }

    @Override
    public String toString() {
        return "new(" + varName + ", " + exp.toString() + ")";
    }
}