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

public class WriteHeapStmt implements IStmt {
    private String varName;
    private Exp exp;

    public WriteHeapStmt(String varName, Exp exp) {
        this.varName = varName;
        this.exp = exp;
    }

    @Override
    public PrgState execute(PrgState state) throws MyException {
        MyIDictionary<String, Value> symTbl = state.getSymTable();
        MyIHeap<Integer, Value> heap = state.getHeap();

        if (!symTbl.isDefined(varName))
            throw new MyException("wH: variable " + varName + " is not defined");

        Value varVal = symTbl.lookup(varName);
        if (!(varVal instanceof RefValue))
            throw new MyException("wH: variable " + varName + " is not a RefValue");

        RefValue refVal = (RefValue) varVal;
        int addr = refVal.getAddr();

        if (!heap.isDefined(addr))
            throw new MyException("wH: address " + addr + " is not defined in heap");

        Value evalVal = exp.eval(symTbl, heap);
        Type locationType = refVal.getLocationType();

        if (!evalVal.getType().equals(locationType))
            throw new MyException("wH: type mismatch: location type " +
                    locationType + " vs expression type " + evalVal.getType());

        heap.update(addr, evalVal);
        return null;
    }

    @Override
    public MyIDictionary<String, Type> typecheck(MyIDictionary<String, Type> typeEnv) throws MyException {
        Type typevar = typeEnv.lookup(varName);
        Type typexp = exp.typecheck(typeEnv);

        if (typevar instanceof RefType) {
            RefType refType = (RefType) typevar;
            if (refType.getInner().equals(typexp))
                return typeEnv;
            else
                throw new MyException("WriteHeap: type of expression and location type do not match");
        } else
            throw new MyException("WriteHeap: variable is not a RefType");
    }

    @Override
    public String toString() {
        return "wH(" + varName + ", " + exp.toString() + ")";
    }
}