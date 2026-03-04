package gui;

import controller.Controller;
import exceptions.MyException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import model.adt.*;
import model.expressions.*;
import model.state.PrgState;
import model.statements.*;
import model.types.IntType;
import model.types.RefType;
import model.values.IntValue;
import repository.IRepository;
import repository.Repository;

import java.util.ArrayList;
import java.util.List;

public class ProgramChooserController {
    @FXML
    private ListView<IStmt> programListView;

    @FXML
    private Button selectButton;

    private List<IStmt> allPrograms;

    @FXML
    public void initialize() {
        allPrograms = new ArrayList<>();
        populatePrograms();

        ObservableList<IStmt> items = FXCollections.observableArrayList(allPrograms);
        programListView.setItems(items);

        // Custom cell factory for displaying program strings
        programListView.setCellFactory(param -> new ListCell<IStmt>() {
            @Override
            protected void updateItem(IStmt item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.toString());
                }
            }
        });
    }

    private void populatePrograms() {
        // Example 1: int v; v=2; Print(v)
        IStmt ex1 = new CompStmt(
                new VarDeclStmt("v", new IntType()),
                new CompStmt(
                        new AssignStmt("v", new ValueExp(new IntValue(2))),
                        new PrintStmt(new VarExp("v"))
                )
        );

        // Example 2: int a; int b; a=2+3*5; b=a+1; Print(b)
        IStmt ex2 = new CompStmt(
                new VarDeclStmt("a", new IntType()),
                new CompStmt(
                        new VarDeclStmt("b", new IntType()),
                        new CompStmt(
                                new AssignStmt("a", new ArithExp('+',
                                        new ValueExp(new IntValue(2)),
                                        new ArithExp('*',
                                                new ValueExp(new IntValue(3)),
                                                new ValueExp(new IntValue(5))))),
                                new CompStmt(
                                        new AssignStmt("b", new ArithExp('+',
                                                new VarExp("a"),
                                                new ValueExp(new IntValue(1)))),
                                        new PrintStmt(new VarExp("b"))
                                )
                        )
                )
        );

        // Example 10: While statement
        IStmt ex10 = new CompStmt(
                new VarDeclStmt("v", new IntType()),
                new CompStmt(
                        new AssignStmt("v", new ValueExp(new IntValue(4))),
                        new CompStmt(
                                new WhileStmt(
                                        new RelationalExp(">", new VarExp("v"), new ValueExp(new IntValue(0))),
                                        new CompStmt(
                                                new PrintStmt(new VarExp("v")),
                                                new AssignStmt("v",
                                                        new ArithExp('-',
                                                                new VarExp("v"),
                                                                new ValueExp(new IntValue(1))
                                                        )
                                                )
                                        )
                                ),
                                new PrintStmt(new VarExp("v"))
                        )
                )
        );

        // Example 12: Fork example
        IStmt ex12 = new CompStmt(
                new VarDeclStmt("v", new IntType()),
                new CompStmt(
                        new VarDeclStmt("a", new RefType(new IntType())),
                        new CompStmt(
                                new AssignStmt("v", new ValueExp(new IntValue(10))),
                                new CompStmt(
                                        new NewStmt("a", new ValueExp(new IntValue(22))),
                                        new CompStmt(
                                                new ForkStmt(
                                                        new CompStmt(
                                                                new WriteHeapStmt("a", new ValueExp(new IntValue(30))),
                                                                new CompStmt(
                                                                        new AssignStmt("v", new ValueExp(new IntValue(32))),
                                                                        new CompStmt(
                                                                                new PrintStmt(new VarExp("v")),
                                                                                new PrintStmt(new ReadHeapExp(new VarExp("a")))
                                                                        )
                                                                )
                                                        )
                                                ),
                                                new CompStmt(
                                                        new PrintStmt(new VarExp("v")),
                                                        new PrintStmt(new ReadHeapExp(new VarExp("a")))
                                                )
                                        )
                                )
                        )
                )
        );

        // Ref int v1; int cnt; new(v1,1); createSemaphore(cnt,rH(v1));
        // fork(acquire(cnt);wh(v1,rh(v1)*10);print(rh(v1));release(cnt));
        // fork(acquire(cnt);wh(v1,rh(v1)*10);wh(v1,rh(v1)*2);print(rh(v1));release(cnt));
        // acquire(cnt); print(rh(v1)-1); release(cnt)
        IStmt ex_semaphore = new CompStmt(
                new VarDeclStmt("v1", new RefType(new IntType())),
                new CompStmt(
                        new VarDeclStmt("cnt", new IntType()),
                        new CompStmt(
                                new NewStmt("v1", new ValueExp(new IntValue(1))),
                                new CompStmt(
                                        new CreateSemaphoreStmt("cnt", new ReadHeapExp(new VarExp("v1"))),
                                        new CompStmt(
                                                new ForkStmt(
                                                        new CompStmt(
                                                                new AcquireStmt("cnt"),
                                                                new CompStmt(
                                                                        new WriteHeapStmt("v1",
                                                                                new ArithExp('*', new ReadHeapExp(new VarExp("v1")), new ValueExp(new IntValue(10)))),
                                                                        new CompStmt(
                                                                                new PrintStmt(new ReadHeapExp(new VarExp("v1"))),
                                                                                new ReleaseStmt("cnt")
                                                                        )
                                                                )
                                                        )
                                                ),
                                                new CompStmt(
                                                        new ForkStmt(
                                                                new CompStmt(
                                                                        new AcquireStmt("cnt"),
                                                                        new CompStmt(
                                                                                new WriteHeapStmt("v1",
                                                                                        new ArithExp('*', new ReadHeapExp(new VarExp("v1")), new ValueExp(new IntValue(10)))),
                                                                                new CompStmt(
                                                                                        new WriteHeapStmt("v1",
                                                                                                new ArithExp('*', new ReadHeapExp(new VarExp("v1")), new ValueExp(new IntValue(2)))),
                                                                                        new CompStmt(
                                                                                                new PrintStmt(new ReadHeapExp(new VarExp("v1"))),
                                                                                                new ReleaseStmt("cnt")
                                                                                        )
                                                                                )
                                                                        )
                                                                )
                                                        ),
                                                        new CompStmt(
                                                                new AcquireStmt("cnt"),
                                                                new CompStmt(
                                                                        new PrintStmt(
                                                                                new ArithExp('-', new ReadHeapExp(new VarExp("v1")), new ValueExp(new IntValue(1)))
                                                                        ),
                                                                        new ReleaseStmt("cnt")
                                                                )
                                                        )
                                                )
                                        )
                                )
                        )
                )
        );


        // Only add programs that pass typecheck
        addProgramIfValid(ex1);
        addProgramIfValid(ex2);
        addProgramIfValid(ex10);
        addProgramIfValid(ex12);
        addProgramIfValid(ex_semaphore);
    }

    private void addProgramIfValid(IStmt program) {
        try {
            program.typecheck(new MyDictionary<>());
            allPrograms.add(program);
        } catch (MyException e) {
            System.out.println("Program failed typecheck: " + e.getMessage());
        }
    }

    @FXML
    private void handleSelectButton() {
        IStmt selectedProgram = programListView.getSelectionModel().getSelectedItem();
        if (selectedProgram == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("No Selection");
            alert.setHeaderText("No Program Selected");
            alert.setContentText("Please select a program from the list.");
            alert.showAndWait();
            return;
        }

        try {
            // Create program state
            PrgState prg = new PrgState(
                    new MyStack<>(),
                    new MyDictionary<>(),
                    new MyList<>(),
                    new MyFileTable(),
                    new MyHeap<>(),
                    new MySemaphoreTable(),
                    selectedProgram
            );

            // Create repository and controller
            IRepository repo = new Repository(prg, "log_gui.txt");
            Controller controller = new Controller(repo);

            // Open main window
            MainWindowController.display(controller);

            // Close this window
            Stage stage = (Stage) selectButton.getScene().getWindow();
            stage.close();

        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Could not initialize program");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }
}