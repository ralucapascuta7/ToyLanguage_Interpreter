package model.statements;

import exceptions.MyException;
import model.adt.MyIDictionary;
import model.adt.MyIFileTable;
import model.expressions.Exp;
import model.state.PrgState;
import model.types.IntType;
import model.types.StringType;
import model.types.Type;
import model.values.IntValue;
import model.values.StringValue;
import model.values.Value;

import java.io.BufferedReader;
import java.io.IOException;

public class ReadFileStmt implements IStmt {
    private Exp exp;
    private String varName;

    public ReadFileStmt(Exp e, String var) {
        exp = e;
        varName = var;
    }

    @Override
    public String toString() {
        return "readFile(" + exp.toString() + ", " + varName + ")";
    }

    @Override
    public PrgState execute(PrgState state) throws MyException {
        MyIDictionary<String, Value> symTbl = state.getSymTable();
        MyIFileTable fileTable = state.getFileTable();

        if (!symTbl.isDefined(varName)) {
            throw new MyException("ReadFile: variable " + varName + " is not defined");
        }

        Value varVal = symTbl.lookup(varName);
        if (!varVal.getType().equals(new IntType())) {
            throw new MyException("ReadFile: variable " + varName + " is not of type int");
        }

        Value val = exp.eval(symTbl, state.getHeap());
        if (!val.getType().equals(new StringType())) {
            throw new MyException("ReadFile: expression is not a string");
        }

        StringValue strVal = (StringValue) val;
        String filename = strVal.getVal();

        if (!fileTable.isDefined(filename)) {
            throw new MyException("ReadFile: file " + filename + " is not opened");
        }

        BufferedReader reader = fileTable.lookup(filename);

        try {
            String line = reader.readLine();
            IntValue value;

            if (line == null) {
                value = new IntValue(0);
            } else {
                value = new IntValue(Integer.parseInt(line));
            }

            symTbl.update(varName, value);
        } catch (IOException e) {
            throw new MyException("ReadFile: error reading from file " + filename + " - " + e.getMessage());
        } catch (NumberFormatException e) {
            throw new MyException("ReadFile: invalid integer format in file " + filename);
        }

        return null;
    }

    @Override
    public MyIDictionary<String, Type> typecheck(MyIDictionary<String, Type> typeEnv) throws MyException {
        Type typexp = exp.typecheck(typeEnv);
        if (!typexp.equals(new StringType()))
            throw new MyException("ReadFile: expression is not a string");

        Type typevar = typeEnv.lookup(varName);
        if (!typevar.equals(new IntType()))
            throw new MyException("ReadFile: variable is not of type int");

        return typeEnv;
    }
}