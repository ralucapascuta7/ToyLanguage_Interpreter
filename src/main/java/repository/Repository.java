package repository;

import exceptions.MyException;
import model.state.PrgState;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class Repository implements IRepository {
    private List<PrgState> programs;
    private String logFilePath;

    public Repository(PrgState prg, String logFile) {
        programs = new ArrayList<>();
        programs.add(prg);
        logFilePath = logFile;
    }

    @Override
    public List<PrgState> getPrgList() {
        return programs;
    }

    @Override
    public void setPrgList(List<PrgState> prgList) {
        programs = prgList;
    }

    @Override
    public void logPrgStateExec(PrgState prg) throws MyException {
        try {
            PrintWriter logFile = new PrintWriter(new BufferedWriter(new FileWriter(logFilePath, true)));
            logFile.println(prg.toString());
            logFile.close();
        } catch (IOException e) {
            throw new MyException("Error writing to log file: " + e.getMessage());
        }
    }
}