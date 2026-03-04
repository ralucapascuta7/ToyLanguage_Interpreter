package repository;

import exceptions.MyException;
import model.state.PrgState;

import java.util.List;

public interface IRepository {
    List<PrgState> getPrgList();
    void setPrgList(List<PrgState> prgList);
    void logPrgStateExec(PrgState prg) throws MyException;
}
