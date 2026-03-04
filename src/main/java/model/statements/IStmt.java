package model.statements;

import exceptions.MyException;
import model.adt.MyIDictionary;
import model.state.PrgState;
import model.types.Type;

public interface IStmt {
    PrgState execute(PrgState state) throws MyException;
    MyIDictionary<String, Type> typecheck(MyIDictionary<String, Type> typeEnv) throws MyException;
    String toString();
}