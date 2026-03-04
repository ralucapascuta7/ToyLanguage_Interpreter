package model.statements;

import exceptions.MyException;
import model.adt.MyIDictionary;
import model.state.PrgState;
import model.types.Type;
import model.values.Value;

public class VarDeclStmt implements IStmt {
    private String name;
    private Type typ;

    public VarDeclStmt(String n, Type t) {
        name = n;
        typ = t;
    }

    @Override
    public String toString() {
        return typ.toString() + " " + name;
    }

    @Override
    public PrgState execute(PrgState state) throws MyException {
        MyIDictionary<String, Value> symTbl = state.getSymTable();

        if (symTbl.isDefined(name)) {
            throw new MyException("Variable " + name + " is already declared");
        }

        symTbl.put(name, typ.defaultValue());
        return null;
    }

    @Override
    public MyIDictionary<String, Type> typecheck(MyIDictionary<String, Type> typeEnv) throws MyException {
        typeEnv.put(name, typ);
        return typeEnv;
    }
}