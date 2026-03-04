package controller;

import exceptions.MyException;
import model.state.PrgState;
import model.values.RefValue;
import model.values.Value;
import repository.IRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class Controller implements IController {
    private IRepository repo;
    private ExecutorService executor;

    public Controller(IRepository r) {
        repo = r;
    }

    public IRepository getRepository() {
        return repo;
    }

    private List<Integer> getAddrFromSymTable(Collection<Value> symTableValues) {
        return symTableValues.stream()
                .filter(v -> v instanceof RefValue)
                .map(v -> ((RefValue) v).getAddr())
                .collect(Collectors.toList());
    }

    private List<Integer> getAddrFromHeap(Collection<Value> heapValues) {
        return heapValues.stream()
                .filter(v -> v instanceof RefValue)
                .map(v -> ((RefValue) v).getAddr())
                .collect(Collectors.toList());
    }

    private Map<Integer, Value> conservativeGarbageCollector(List<PrgState> prgList, Map<Integer, Value> heap) {
        List<Integer> allSymTableAddr = prgList.stream()
                .flatMap(p -> getAddrFromSymTable(p.getSymTable().getContent().values()).stream())
                .collect(Collectors.toList());

        List<Integer> heapAddr = getAddrFromHeap(heap.values());

        List<Integer> allAddr = new ArrayList<>();
        allAddr.addAll(allSymTableAddr);
        allAddr.addAll(heapAddr);

        return heap.entrySet().stream()
                .filter(e -> allAddr.contains(e.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private List<PrgState> removeCompletedPrg(List<PrgState> inPrgList) {
        return inPrgList.stream()
                .filter(p -> p.isNotCompleted())
                .collect(Collectors.toList());
    }

    public void oneStepForAllPrg(List<PrgState> prgList) throws InterruptedException {
        // Log before execution
        prgList.forEach(prg -> {
            try {
                repo.logPrgStateExec(prg);
            } catch (MyException e) {
                System.out.println("Error logging: " + e.getMessage());
            }
        });

        // Prepare callables
        List<Callable<PrgState>> callList = prgList.stream()
                .map((PrgState p) -> (Callable<PrgState>) (() -> {
                    return p.oneStep();
                }))
                .collect(Collectors.toList());

        // Execute concurrently
        if (executor == null || executor.isShutdown()) {
            executor = Executors.newFixedThreadPool(2);
        }

        List<PrgState> newPrgList = executor.invokeAll(callList).stream()
                .map(future -> {
                    try {
                        return future.get();
                    } catch (InterruptedException | ExecutionException e) {
                        System.out.println("Error during execution: " + e.getMessage());
                        return null;
                    }
                })
                .filter(p -> p != null)
                .collect(Collectors.toList());

        // Add new created threads
        prgList.addAll(newPrgList);

        // Garbage collection
        if (!prgList.isEmpty()) {
            prgList.get(0).getHeap().setContent(
                    conservativeGarbageCollector(prgList, prgList.get(0).getHeap().getContent())
            );
        }

        // Log after execution
        prgList.forEach(prg -> {
            try {
                repo.logPrgStateExec(prg);
            } catch (MyException e) {
                System.out.println("Error logging: " + e.getMessage());
            }
        });

        // Save in repository
        repo.setPrgList(prgList);
    }

    @Override
    public void allStep() throws MyException {
        executor = Executors.newFixedThreadPool(2);

        List<PrgState> prgList = removeCompletedPrg(repo.getPrgList());

        while (prgList.size() > 0) {
            if (prgList.size() > 0) {
                prgList.get(0).getHeap().setContent(
                        conservativeGarbageCollector(prgList, prgList.get(0).getHeap().getContent())
                );
            }

            try {
                oneStepForAllPrg(prgList);
            } catch (InterruptedException e) {
                throw new MyException("Execution interrupted: " + e.getMessage());
            }

            prgList = removeCompletedPrg(repo.getPrgList());
        }

        executor.shutdownNow();
        repo.setPrgList(prgList);
    }
}