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
        System.out.println("1. Jon Snow - Warrior (300 HP, 30 ATK, 4 DEF)");
        System.out.println("2. The Hound - Warrior (400 HP, 20 ATK, 6 DEF)");
        System.out.println("3. Melisandre - Mage (100 HP, 5 ATK, 1 DEF)");
        System.out.println("4. Thoros of Myr - Mage (250 HP, 25 ATK, 4 DEF)");
        System.out.println("5. Arya Stark - Rogue (150 HP, 40 ATK, 2 DEF)");
        System.out.println("6. Bronn - Rogue (250 HP, 35 ATK, 3 DEF)");
        System.out.println("7. Ygritte - Hunter (220 HP, 30 ATK, 2 DEF)");
    }


    @Override
    public void onMessage(String msg) {
        System.out.println(msg);
    }
}