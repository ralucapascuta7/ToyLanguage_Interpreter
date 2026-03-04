package view;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

public class TextMenu {
    private final Map<String, Command> commands = new LinkedHashMap<>();

    public void addCommand(Command c) {
        if (commands.containsKey(c.getKey())) {
            throw new RuntimeException("Duplicate command key: " + c.getKey());
        }
        commands.put(c.getKey(), c);
    }

    private void printMenu() {
        System.out.println();
        System.out.println("========== TOY LANGUAGE INTERPRETER ==========");
        for (Command com : commands.values()) {
            System.out.printf("%4s : %s%n", com.getKey(), com.getDescription());
        }
        System.out.println("==============================================");
    }

    public void show() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            printMenu();
            System.out.print("Input the option: ");
            String key = scanner.nextLine().trim();

            Command com = commands.get(key);
            if (com == null) {
                System.out.println("!!! Invalid Option !!!");
                continue;
            }
            com.execute();
        }
    }
}
