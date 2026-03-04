package model.expressions;

import exceptions.MyException;
import model.adt.MyIDictionary;
import model.adt.MyIHeap;
import model.types.RefType;
import model.types.Type;
import model.values.RefValue;
import model.values.Value;

public class ReadHeapExp implements Exp {
    private Exp exp;

    public ReadHeapExp(Exp e) {
        exp = e;
    }

    @Override
    public Value eval(MyIDictionary<String, Value> tbl, MyIHeap<Integer, Value> heap) throws MyException {
        Value v = exp.eval(tbl, heap);
        if (!(v instanceof RefValue))
            throw new MyException("rH: expression is not a RefValue");
        RefValue rv = (RefValue) v;
        int addr = rv.getAddr();
        if (!heap.isDefined(addr))
            throw new MyException("rH: address " + addr + " not defined in heap");
        return heap.lookup(addr);
    }

    @Override
    public Type typecheck(MyIDictionary<String, Type> typeEnv) throws MyException {
        Type typ = exp.typecheck(typeEnv);
        if (typ instanceof RefType) {
            RefType reft = (RefType) typ;
            return reft.getInner();
        } else
            throw new MyException("ReadHeapExp: the rH argument is not a Ref Type");
    }

    @Override
    public String toString() {
        return "rH(" + exp.toString() + ")";
    }
}