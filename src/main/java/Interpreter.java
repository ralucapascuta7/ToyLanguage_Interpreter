import controller.Controller;
import exceptions.MyException;
import model.adt.*;
import model.expressions.*;
import model.state.PrgState;
import model.statements.*;
import model.types.BoolType;
import model.types.IntType;
import model.types.RefType;
import model.types.StringType;
import model.values.BoolValue;
import model.values.IntValue;
import model.values.StringValue;
import repository.IRepository;
import repository.Repository;
import view.ExitCommand;
import view.RunExample;
import view.TextMenu;

import java.util.ArrayList;
import java.util.List;

public class Interpreter {

    private static PrgState makeState(IStmt program) {
        return new PrgState(
                new MyStack<>(),
                new MyDictionary<>(),
                new MyList<>(),
                new MyFileTable(),
                new MyHeap<>(),
                new MySemaphoreTable(),
                program
        );
    }

    public static void main(String[] args) {
        System.out.println("Welcome to Toy Language Interpreter!");

        // =========================
        // EXAMPLES (1..14)
        // =========================

        // 1: int v; v=2; print(v)
        IStmt ex1 = new CompStmt(
                new VarDeclStmt("v", new IntType()),
                new CompStmt(
                        new AssignStmt("v", new ValueExp(new IntValue(2))),
                        new PrintStmt(new VarExp("v"))
                )
        );

        // 2: int a; int b; a=2+3*5; b=a+1; print(b)
        IStmt ex2 = new CompStmt(
                new VarDeclStmt("a", new IntType()),
                new CompStmt(
                        new VarDeclStmt("b", new IntType()),
                        new CompStmt(
                                new AssignStmt("a",
                                        new ArithExp('+',
                                                new ValueExp(new IntValue(2)),
                                                new ArithExp('*',
                                                        new ValueExp(new IntValue(3)),
                                                        new ValueExp(new IntValue(5))
                                                )
                                        )
                                ),
                                new CompStmt(
                                        new AssignStmt("b",
                                                new ArithExp('+', new VarExp("a"), new ValueExp(new IntValue(1)))
                                        ),
                                        new PrintStmt(new VarExp("b"))
                                )
                        )
                )
        );

        // 3: bool a; int v; a=true; if(a) v=2 else v=3; print(v)
        IStmt ex3 = new CompStmt(
                new VarDeclStmt("a", new BoolType()),
                new CompStmt(
                        new VarDeclStmt("v", new IntType()),
                        new CompStmt(
                                new AssignStmt("a", new ValueExp(new BoolValue(true))),
                                new CompStmt(
                                        new IfStmt(
                                                new VarExp("a"),
                                                new AssignStmt("v", new ValueExp(new IntValue(2))),
                                                new AssignStmt("v", new ValueExp(new IntValue(3)))
                                        ),
                                        new PrintStmt(new VarExp("v"))
                                )
                        )
                )
        );

        // 4: string varf; varf="test.in"; openRFile(varf); int varc;
        // readFile(varf,varc); print(varc); readFile(varf,varc); print(varc); closeRFile(varf)
        IStmt ex4 = new CompStmt(
                new VarDeclStmt("varf", new StringType()),
                new CompStmt(
                        new AssignStmt("varf", new ValueExp(new StringValue("test.in"))),
                        new CompStmt(
                                new OpenRFileStmt(new VarExp("varf")),
                                new CompStmt(
                                        new VarDeclStmt("varc", new IntType()),
                                        new CompStmt(
                                                new ReadFileStmt(new VarExp("varf"), "varc"),
                                                new CompStmt(
                                                        new PrintStmt(new VarExp("varc")),
                                                        new CompStmt(
                                                                new ReadFileStmt(new VarExp("varf"), "varc"),
                                                                new CompStmt(
                                                                        new PrintStmt(new VarExp("varc")),
                                                                        new CloseRFileStmt(new VarExp("varf"))
                                                                )
                                                        )
                                                )
                                        )
                                )
                        )
                )
        );

        // 5: int a; int b; a=10; b=5; if(a>b) print(a) else print(b)
        IStmt ex5 = new CompStmt(
                new VarDeclStmt("a", new IntType()),
                new CompStmt(
                        new VarDeclStmt("b", new IntType()),
                        new CompStmt(
                                new AssignStmt("a", new ValueExp(new IntValue(10))),
                                new CompStmt(
                                        new AssignStmt("b", new ValueExp(new IntValue(5))),
                                        new IfStmt(
                                                new RelationalExp(">", new VarExp("a"), new VarExp("b")),
                                                new PrintStmt(new VarExp("a")),
                                                new PrintStmt(new VarExp("b"))
                                        )
                                )
                        )
                )
        );

        // 6: int a; int b; int c; a=5; b=10; c=15; if(a<=b and b<c) print(1) else print(0)
        IStmt ex6 = new CompStmt(
                new VarDeclStmt("a", new IntType()),
                new CompStmt(
                        new VarDeclStmt("b", new IntType()),
                        new CompStmt(
                                new VarDeclStmt("c", new IntType()),
                                new CompStmt(
                                        new AssignStmt("a", new ValueExp(new IntValue(5))),
                                        new CompStmt(
                                                new AssignStmt("b", new ValueExp(new IntValue(10))),
                                                new CompStmt(
                                                        new AssignStmt("c", new ValueExp(new IntValue(15))),
                                                        new IfStmt(
                                                                new LogicExp("and",
                                                                        new RelationalExp("<=", new VarExp("a"), new VarExp("b")),
                                                                        new RelationalExp("<", new VarExp("b"), new VarExp("c"))
                                                                ),
                                                                new PrintStmt(new ValueExp(new IntValue(1))),
                                                                new PrintStmt(new ValueExp(new IntValue(0)))
                                                        )
                                                )
                                        )
                                )
                        )
                )
        );

        // 7: Ref(int) v; new(v,20); Ref(Ref(int)) a; new(a,v); print(v); print(a)
        IStmt ex7 = new CompStmt(
                new VarDeclStmt("v", new RefType(new IntType())),
                new CompStmt(
                        new NewStmt("v", new ValueExp(new IntValue(20))),
                        new CompStmt(
                                new VarDeclStmt("a", new RefType(new RefType(new IntType()))),
                                new CompStmt(
                                        new NewStmt("a", new VarExp("v")),
                                        new CompStmt(
                                                new PrintStmt(new VarExp("v")),
                                                new PrintStmt(new VarExp("a"))
                                        )
                                )
                        )
                )
        );

        // 8: Ref(int) v; new(v,20); Ref(Ref(int)) a; new(a,v); print(rH(v)); print(rH(rH(a))+5)
        IStmt ex8 = new CompStmt(
                new VarDeclStmt("v", new RefType(new IntType())),
                new CompStmt(
                        new NewStmt("v", new ValueExp(new IntValue(20))),
                        new CompStmt(
                                new VarDeclStmt("a", new RefType(new RefType(new IntType()))),
                                new CompStmt(
                                        new NewStmt("a", new VarExp("v")),
                                        new CompStmt(
                                                new PrintStmt(new ReadHeapExp(new VarExp("v"))),
                                                new PrintStmt(
                                                        new ArithExp('+',
                                                                new ReadHeapExp(new ReadHeapExp(new VarExp("a"))),
                                                                new ValueExp(new IntValue(5))
                                                        )
                                                )
                                        )
                                )
                        )
                )
        );

        // 9: Ref(int) v; new(v,20); print(rH(v)); wH(v,30); print(rH(v)+5)
        IStmt ex9 = new CompStmt(
                new VarDeclStmt("v", new RefType(new IntType())),
                new CompStmt(
                        new NewStmt("v", new ValueExp(new IntValue(20))),
                        new CompStmt(
                                new PrintStmt(new ReadHeapExp(new VarExp("v"))),
                                new CompStmt(
                                        new WriteHeapStmt("v", new ValueExp(new IntValue(30))),
                                        new PrintStmt(
                                                new ArithExp('+',
                                                        new ReadHeapExp(new VarExp("v")),
                                                        new ValueExp(new IntValue(5))
                                                )
                                        )
                                )
                        )
                )
        );

        // 10: int v; v=4; while(v>0){ print(v); v=v-1 }; print(v)
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
                                                        new ArithExp('-', new VarExp("v"), new ValueExp(new IntValue(1)))
                                                )
                                        )
                                ),
                                new PrintStmt(new VarExp("v"))
                        )
                )
        );

        // 11: Ref(int) v; new(v,20); Ref(Ref(int)) a; new(a,v); new(v,30); print(rH(rH(a)))
        IStmt ex11 = new CompStmt(
                new VarDeclStmt("v", new RefType(new IntType())),
                new CompStmt(
                        new NewStmt("v", new ValueExp(new IntValue(20))),
                        new CompStmt(
                                new VarDeclStmt("a", new RefType(new RefType(new IntType()))),
                                new CompStmt(
                                        new NewStmt("a", new VarExp("v")),
                                        new CompStmt(
                                                new NewStmt("v", new ValueExp(new IntValue(30))),
                                                new PrintStmt(new ReadHeapExp(new ReadHeapExp(new VarExp("a"))))
                                        )
                                )
                        )
                )
        );

        // 12: int v; Ref(int) a; v=10; new(a,22);
        // fork( wH(a,30); v=32; print(v); print(rH(a)) );
        // print(v); print(rH(a))
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

        // 13: Typechecker test - should FAIL typecheck
        // int v; v=true; print(v)
        IStmt ex13 = new CompStmt(
                new VarDeclStmt("v", new IntType()),
                new CompStmt(
                        new AssignStmt("v", new ValueExp(new BoolValue(true))),
                        new PrintStmt(new VarExp("v"))
                )
        );

        // 14: Typechecker test - should PASS
        // int v; bool b; v=5; b=v>3; if(b) then print(v) else print(0)
        IStmt ex14 = new CompStmt(
                new VarDeclStmt("v", new IntType()),
                new CompStmt(
                        new VarDeclStmt("b", new BoolType()),
                        new CompStmt(
                                new AssignStmt("v", new ValueExp(new IntValue(5))),
                                new CompStmt(
                                        new AssignStmt("b",
                                                new RelationalExp(">", new VarExp("v"), new ValueExp(new IntValue(3)))
                                        ),
                                        new IfStmt(
                                                new VarExp("b"),
                                                new PrintStmt(new VarExp("v")),
                                                new PrintStmt(new ValueExp(new IntValue(0)))
                                        )
                                )
                        )
                )
        );

        List<IStmt> programs = new ArrayList<>();
        programs.add(ex1);
        programs.add(ex2);
        programs.add(ex3);
        programs.add(ex4);
        programs.add(ex5);
        programs.add(ex6);
        programs.add(ex7);
        programs.add(ex8);
        programs.add(ex9);
        programs.add(ex10);
        programs.add(ex11);
        programs.add(ex12);
        programs.add(ex13);
        programs.add(ex14);

        TextMenu menu = new TextMenu();
        menu.addCommand(new ExitCommand("0", "exit"));

        for (int i = 0; i < programs.size(); i++) {
            IStmt stmt = programs.get(i);
            String key = String.valueOf(i + 1);

            try {
                stmt.typecheck(new MyDictionary<>());
            } catch (MyException e) {
                menu.addCommand(new RunExample(
                        key,
                        stmt.toString(),
                        null
                ));
                continue;
            }

            PrgState prg = makeState(stmt);
            IRepository repo = new Repository(prg, "log" + key + ".txt");
            Controller ctrl = new Controller(repo);

            menu.addCommand(new RunExample(key, stmt.toString(), ctrl));
        }

        menu.show();
    }
}
