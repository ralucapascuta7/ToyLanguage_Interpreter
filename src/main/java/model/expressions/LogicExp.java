package model.expressions;

import exceptions.MyException;
import model.adt.MyIDictionary;
import model.adt.MyIHeap;
import model.types.BoolType;
import model.types.Type;
import model.values.BoolValue;
import model.values.Value;

public class LogicExp implements Exp {
    private Exp e1;
    private Exp e2;
    private String op;

    public LogicExp(String operation, Exp exp1, Exp exp2) {
        e1 = exp1;
        e2 = exp2;
        op = operation;
    }

    @Override
    public Value eval(MyIDictionary<String, Value> tbl, MyIHeap<Integer, Value> heap) throws MyException {
        Value v1 = e1.eval(tbl, heap);
        if (v1.getType().equals(new BoolType())) {
            Value v2 = e2.eval(tbl, heap);
            if (v2.getType().equals(new BoolType())) {
                BoolValue b1 = (BoolValue) v1;
                BoolValue b2 = (BoolValue) v2;
                boolean bool1 = b1.getVal();
                boolean bool2 = b2.getVal();

                if (op.equals("and"))
                    return new BoolValue(bool1 && bool2);
                else if (op.equals("or"))
                    return new BoolValue(bool1 || bool2);
                else
                    throw new MyException("Invalid logical operator: " + op);
            } else {
                throw new MyException("Second operand is not a boolean");
            }
        } else {
            throw new MyException("First operand is not a boolean");
        }
    }

    @Override
    public Type typecheck(MyIDictionary<String, Type> typeEnv) throws MyException {
        Type typ1, typ2;
        typ1 = e1.typecheck(typeEnv);
        typ2 = e2.typecheck(typeEnv);

        if (typ1.equals(new BoolType())) {
            if (typ2.equals(new BoolType())) {
                return new BoolType();
            } else
                throw new MyException("LogicExp: second operand is not a boolean");
        } else
            throw new MyException("LogicExp: first operand is not a boolean");
    }

    @Override
    public String toString() {
        return e1.toString() + " " + op + " " + e2.toString();
    }
}