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
import java.io.IOException;

public class CloseRFileStmt implements IStmt {
    private Exp exp;

    public CloseRFileStmt(Exp e) {
        exp = e;
    }

    @Override
    public String toString() {
        return "closeRFile(" + exp.toString() + ")";
    }

    @Override
    public PrgState execute(PrgState state) throws MyException {
        Value val = exp.eval(state.getSymTable(), state.getHeap());

        if (!val.getType().equals(new StringType())) {
            throw new MyException("CloseRFile: expression is not a string");
        }

        StringValue strVal = (StringValue) val;
        String filename = strVal.getVal();

        MyIFileTable fileTable = state.getFileTable();

        if (!fileTable.isDefined(filename)) {
            throw new MyException("CloseRFile: file " + filename + " is not in the FileTable");
        }

        BufferedReader reader = fileTable.lookup(filename);

        try {
            reader.close();
        } catch (IOException e) {
            throw new MyException("CloseRFile: error closing file " + filename + " - " + e.getMessage());
        }

        fileTable.remove(filename);
        return null;
    }

    @Override
    public MyIDictionary<String, Type> typecheck(MyIDictionary<String, Type> typeEnv) throws MyException {
        Type typ = exp.typecheck(typeEnv);
        if (typ.equals(new StringType()))
            return typeEnv;
        else
            throw new MyException("CloseRFile: expression is not a string");
    }
}