package view;

import controller.Controller;
import exceptions.MyException;

public class RunExample extends Command {
    private Controller ctr;

    public RunExample(String key, String desc, Controller ctr) {
        super(key, desc);
        this.ctr = ctr;
    }

    @Override
    public void execute() {
        // Check if this program failed typecheck
        if (ctr == null) {
            System.out.println("╔════════════════════════════════════════════════════════════════════════════╗");
            System.out.println("║                    ✗ TYPECHECK FAILED                                     ║");
            System.out.println("╠════════════════════════════════════════════════════════════════════════════╣");
            System.out.println("║ This program contains type errors and cannot be executed.                 ║");
            System.out.println("╚════════════════════════════════════════════════════════════════════════════╝");

            System.out.println("\nPress ENTER to return to menu...");
            try {
                System.in.read();
            } catch (Exception e) {
                // Ignore
            }
            return;
        }

        try {
            System.out.println("╔════════════════════════════════════════════════════════════════════════════╗");
            System.out.println("║                          PROGRAM EXECUTION                                ║");
            System.out.println("╚════════════════════════════════════════════════════════════════════════════╝\n");

            ctr.allStep();

            System.out.println("\n╔════════════════════════════════════════════════════════════════════════════╗");
            System.out.println("║                    ✓ PROGRAM EXECUTED SUCCESSFULLY!                       ║");
            System.out.println("╚════════════════════════════════════════════════════════════════════════════╝");

            System.out.println("\nPress ENTER to return to menu...");
            try {
                System.in.read();
            } catch (Exception e) {
                // Ignore
            }

        } catch (MyException e) {
            System.out.println("\n╔════════════════════════════════════════════════════════════════════════════╗");
            System.out.println("║                         ✗ EXECUTION ERROR                                 ║");
            System.out.println("╠════════════════════════════════════════════════════════════════════════════╣");
            System.out.printf("║ %-74s ║%n", e.getMessage());
            System.out.println("╚════════════════════════════════════════════════════════════════════════════╝");

            System.out.println("\nPress ENTER to return to menu...");
            try {
                System.in.read();
            } catch (Exception ex) {
                // Ignore
            }
        }
    }
}