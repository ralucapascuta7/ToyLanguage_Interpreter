package gui;

import controller.Controller;
import exceptions.MyException;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.adt.MyIDictionary;
import model.adt.MyIStack;
import model.state.PrgState;
import model.statements.IStmt;
import model.values.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javafx.util.Pair;

public class MainWindowController {
    @FXML
    private TextField nrPrgStatesTextField;

    @FXML
    private TableView<HeapEntry> heapTableView;
    @FXML
    private TableColumn<HeapEntry, Integer> heapAddressColumn;
    @FXML
    private TableColumn<HeapEntry, String> heapValueColumn;

    @FXML
    private ListView<String> outListView;

    @FXML
    private ListView<String> fileTableListView;

    @FXML
    private ListView<Integer> prgStateIdListView;

    @FXML
    private TableView<SymTableEntry> symTableView;
    @FXML
    private TableColumn<SymTableEntry, String> symTableVarNameColumn;
    @FXML
    private TableColumn<SymTableEntry, String> symTableValueColumn;

    @FXML
    private TableView<SemaphoreEntry> semaphoreTableView;
    @FXML
    private TableColumn<SemaphoreEntry, Integer> semaphoreIndexColumn;
    @FXML
    private TableColumn<SemaphoreEntry, Integer> semaphoreValueColumn;
    @FXML
    private TableColumn<SemaphoreEntry, String> semaphoreListColumn;

    @FXML
    private ListView<String> exeStackListView;

    @FXML
    private Button runOneStepButton;

    private Controller controller;

    public static void display(Controller controller) throws Exception {
        FXMLLoader loader = new FXMLLoader(MainWindowController.class.getResource("/MainWindow.fxml"));
        Parent root = loader.load();

        MainWindowController mainController = loader.getController();
        mainController.setController(controller);

        Stage stage = new Stage();
        stage.setTitle("ToyLanguage Interpreter - Main Window");
        stage.setScene(new Scene(root, 1200, 700));
        stage.show();

        mainController.populateUI();
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    @FXML
    public void initialize() {
        // Setup table columns
        heapAddressColumn.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getAddress()).asObject());
        heapValueColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getValue()));

        symTableVarNameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getVarName()));
        symTableValueColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getValue()));

        // Add listener for PrgState selection
        prgStateIdListView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        populateExecutionStack(newValue);
                        populateSymTable(newValue);
                    }
                });

        semaphoreIndexColumn.setCellValueFactory(data ->
                new SimpleIntegerProperty(data.getValue().getIndex()).asObject());
        semaphoreValueColumn.setCellValueFactory(data ->
                new SimpleIntegerProperty(data.getValue().getValue()).asObject());
        semaphoreListColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getList()));
    }

    private void populateUI() {
        List<PrgState> prgList = controller.getRepository().getPrgList();

        // Number of PrgStates
        nrPrgStatesTextField.setText(String.valueOf(prgList.size()));

        // Heap Table
        if (!prgList.isEmpty()) {
            Map<Integer, Value> heap = prgList.get(0).getHeap().getContent();
            ObservableList<HeapEntry> heapEntries = FXCollections.observableArrayList();
            for (Map.Entry<Integer, Value> entry : heap.entrySet()) {
                heapEntries.add(new HeapEntry(entry.getKey(), entry.getValue().toString()));
            }
            heapTableView.setItems(heapEntries);
        }

        // Out
        if (!prgList.isEmpty()) {
            List<Value> outList = prgList.get(0).getOut().getContent();
            ObservableList<String> outItems = FXCollections.observableArrayList(
                    outList.stream().map(Value::toString).collect(Collectors.toList())
            );
            outListView.setItems(outItems);
        }

        // FileTable
        if (!prgList.isEmpty()) {
            ObservableList<String> fileItems = FXCollections.observableArrayList(
                    prgList.get(0).getFileTable().getContent().keySet()
            );
            fileTableListView.setItems(fileItems);
        }

        // PrgState IDs
        ObservableList<Integer> idItems = FXCollections.observableArrayList(
                prgList.stream().map(PrgState::getId).collect(Collectors.toList())
        );
        prgStateIdListView.setItems(idItems);

        // Select first PrgState by default
        if (!prgList.isEmpty()) {
            prgStateIdListView.getSelectionModel().selectFirst();
        }

        if (!prgList.isEmpty()) {
            Map<Integer, Pair<Integer, List<Integer>>> semaphores =
                    prgList.get(0).getSemaphoreTable().getContent();
            ObservableList<SemaphoreEntry> semaphoreEntries = FXCollections.observableArrayList();
            for (Map.Entry<Integer, Pair<Integer, List<Integer>>> entry : semaphores.entrySet()) {
                semaphoreEntries.add(new SemaphoreEntry(
                        entry.getKey(),
                        entry.getValue().getKey(),
                        entry.getValue().getValue().toString()
                ));
            }
            semaphoreTableView.setItems(semaphoreEntries);
        }
    }

    private void populateExecutionStack(int prgStateId) {
        List<PrgState> prgList = controller.getRepository().getPrgList();
        PrgState selectedPrg = prgList.stream()
                .filter(p -> p.getId() == prgStateId)
                .findFirst()
                .orElse(null);

        if (selectedPrg != null) {
            MyIStack<IStmt> stack = selectedPrg.getStk();
            List<String> stackStrings = new ArrayList<>();

            // Convert stack to list (need to access without modifying)
            List<IStmt> stackList = new ArrayList<>();
            try {
                while (!stack.isEmpty()) {
                    stackList.add(stack.pop());
                }
                // Push back
                for (int i = stackList.size() - 1; i >= 0; i--) {
                    stack.push(stackList.get(i));
                }
                // Convert to strings
                for (IStmt stmt : stackList) {
                    stackStrings.add(stmt.toString());
                }
            } catch (MyException e) {
                // Stack is empty
            }

            ObservableList<String> stackItems = FXCollections.observableArrayList(stackStrings);
            exeStackListView.setItems(stackItems);
        }
    }

    private void populateSymTable(int prgStateId) {
        List<PrgState> prgList = controller.getRepository().getPrgList();
        PrgState selectedPrg = prgList.stream()
                .filter(p -> p.getId() == prgStateId)
                .findFirst()
                .orElse(null);

        if (selectedPrg != null) {
            MyIDictionary<String, Value> symTable = selectedPrg.getSymTable();
            ObservableList<SymTableEntry> entries = FXCollections.observableArrayList();

            for (Map.Entry<String, Value> entry : symTable.getContent().entrySet()) {
                entries.add(new SymTableEntry(entry.getKey(), entry.getValue().toString()));
            }

            symTableView.setItems(entries);
        }
    }

    @FXML
    private void handleRunOneStep() {
        if (controller == null) {
            return;
        }

        try {
            // Get current program list
            List<PrgState> prgList = controller.getRepository().getPrgList();

            if (prgList.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Execution Complete");
                alert.setHeaderText("Program execution finished");
                alert.setContentText("All threads have completed execution.");
                alert.showAndWait();
                return;
            }

            // Remove completed programs
            prgList = prgList.stream()
                    .filter(PrgState::isNotCompleted)
                    .collect(Collectors.toList());

            if (prgList.isEmpty()) {
                controller.getRepository().setPrgList(prgList);
                populateUI();

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Execution Complete");
                alert.setHeaderText("Program execution finished");
                alert.setContentText("All threads have completed execution.");
                alert.showAndWait();
                return;
            }

            // Execute one step for all programs
            controller.oneStepForAllPrg(prgList);

            // Update UI
            populateUI();

        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Execution Error");
            alert.setHeaderText("Error during execution");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    // Inner classes for TableView entries
    public static class HeapEntry {
        private final Integer address;
        private final String value;

        public HeapEntry(Integer address, String value) {
            this.address = address;
            this.value = value;
        }

        public Integer getAddress() {
            return address;
        }

        public String getValue() {
            return value;
        }
    }

    public static class SymTableEntry {
        private final String varName;
        private final String value;

        public SymTableEntry(String varName, String value) {
            this.varName = varName;
            this.value = value;
        }

        public String getVarName() {
            return varName;
        }

        public String getValue() {
            return value;
        }
    }

    public static class SemaphoreEntry {
        private final Integer index;
        private final Integer value;
        private final String list;

        public SemaphoreEntry(Integer index, Integer value, String list) {
            this.index = index;
            this.value = value;
            this.list = list;
        }

        public Integer getIndex() { return index; }
        public Integer getValue() { return value; }
        public String getList() { return list; }
    }
}