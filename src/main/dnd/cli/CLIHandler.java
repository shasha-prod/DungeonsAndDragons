package dnd.cli;

import dnd.business.GameObserver;

import java.util.Scanner;

public class CLIHandler implements GameObserver {
    private Scanner scanner = new Scanner(System.in);

    @Override
    public void onMessage(String message) {
        System.out.println(message);
    }
}
