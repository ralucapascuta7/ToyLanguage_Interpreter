package model.statements;

import exceptions.MyException;
import model.adt.MyIDictionary;
import model.adt.MyIStack;
import model.adt.MyStack;
import model.state.PrgState;
import model.types.Type;

public class ForkStmt implements IStmt {
    private IStmt stmt;

    public ForkStmt(IStmt stmt) {
        this.stmt = stmt;
    }

    @Override
    public PrgState execute(PrgState state) throws MyException {
        MyIStack<IStmt> newStack = new MyStack<>();
        //newStack.push(stmt);

        MyIDictionary<String, model.values.Value> clonedSymTable = state.getSymTable().deepCopy();

        return new PrgState(
                newStack,
                clonedSymTable,
                state.getOut(),
                state.getFileTable(),
                state.getHeap(),
                state.getSemaphoreTable(),
                stmt
        );
    }

    @Override
    public MyIDictionary<String, Type> typecheck(MyIDictionary<String, Type> typeEnv) throws MyException {
        stmt.typecheck(typeEnv.deepCopy());
        return typeEnv;
    }

    @Override
    public String toString() {
        return "fork(" + stmt.toString() + ")";
    }
}