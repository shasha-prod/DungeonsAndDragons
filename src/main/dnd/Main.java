package dnd;

import dnd.cli.CLIHandler;
import dnd.business.GameManager;

public class Main {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: java -jar hw3.jar <path to levels>");
            return;
        }
        CLIHandler cli = new CLIHandler();
        GameManager game = new GameManager(cli);
        game.run(args[0]);
    }
}