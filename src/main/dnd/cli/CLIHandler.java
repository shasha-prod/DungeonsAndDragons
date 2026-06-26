package dnd.cli;
import dnd.business.GameObserver;
import dnd.business.board.GameBoard;
import dnd.business.units.Player;
import dnd.business.units.Unit;

import java.util.Scanner;

public class CLIHandler implements GameObserver {
    private final Scanner scanner;

    public CLIHandler() {
        this.scanner = new Scanner(System.in);
    }

    public String getPlayerAction() {
        return scanner.nextLine();
    }

    @Override
    public void onStart() {
        System.out.println("Select player:");
    }


    @Override
    public void onMessage(String msg) {
        System.out.println(msg);
    }
}