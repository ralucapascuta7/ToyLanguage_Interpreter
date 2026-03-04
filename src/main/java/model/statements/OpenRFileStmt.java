package model.statements;

import exceptions.MyException;
import model.adt.MyIDictionary;
import model.adt.MyIFileTable;
import model.expressions.Exp;
import model.state.PrgState;
import model.types.StringType;
import model.types.Type;
import model.values.StringValue;
import model.values.Value;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class OpenRFileStmt implements IStmt {
    private Exp exp;

    public OpenRFileStmt(Exp e) {
        exp = e;
    }

    @Override
    public String toString() {
        return "openRFile(" + exp.toString() + ")";
    }

    @Override
    public PrgState execute(PrgState state) throws MyException {
        Value val = exp.eval(state.getSymTable(), state.getHeap());

        if (!val.getType().equals(new StringType())) {
            throw new MyException("OpenRFile: expression is not a string");
        }

        StringValue strVal = (StringValue) val;
        String filename = strVal.getVal();

        MyIFileTable fileTable = state.getFileTable();

        if (fileTable.isDefined(filename)) {
            throw new MyException("OpenRFile: file " + filename + " is already opened");
        }

        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            fileTable.put(filename, reader);
        } catch (IOException e) {
            throw new MyException("OpenRFile: error opening file " + filename + " - " + e.getMessage());
        }

        return null;
    }

    @Override
    public MyIDictionary<String, Type> typecheck(MyIDictionary<String, Type> typeEnv) throws MyException {
        Type typ = exp.typecheck(typeEnv);
        if (typ.equals(new StringType()))
            return typeEnv;
        else
            throw new MyException("OpenRFile: expression is not a string");
    }
}