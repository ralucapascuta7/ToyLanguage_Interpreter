package model.statements;

import exceptions.MyException;
import model.adt.MyIDictionary;
import model.expressions.Exp;
import model.state.PrgState;
import model.types.Type;
import model.values.Value;

public class AssignStmt implements IStmt {
    private String id;
    private Exp exp;

    public AssignStmt(String name, Exp e) {
        id = name;
        exp = e;
    }

    @Override
    public String toString() {
        return id + " = " + exp.toString();
    }

    @Override
    public PrgState execute(PrgState state) throws MyException {
        MyIDictionary<String, Value> symTbl = state.getSymTable();

        if (symTbl.isDefined(id)) {
            Value val = exp.eval(symTbl, state.getHeap());
            Value tableVal = symTbl.lookup(id);
            Type typId = tableVal.getType();

            if (val.getType().equals(typId)) {
                symTbl.update(id, val);
            } else {
                throw new MyException("Declared type of variable " + id +
                        " and type of the assigned expression do not match");
            }
        } else {
            throw new MyException("The used variable " + id + " was not declared before");
        }
        return null;
    }

    @Override
    public MyIDictionary<String, Type> typecheck(MyIDictionary<String, Type> typeEnv) throws MyException {
        Type typevar = typeEnv.lookup(id);
        Type typexp = exp.typecheck(typeEnv);
        if (typevar.equals(typexp))
            return typeEnv;
        else
            throw new MyException("Assignment: right hand side and left hand side have different types");
    }
}