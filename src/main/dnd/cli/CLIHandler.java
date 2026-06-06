package dnd.cli;

import dnd.business.GameObserver;
import dnd.business.board.GameBoard;
import dnd.business.units.Player;
import dnd.business.units.Unit;

import java.util.Scanner;

public class CLIHandler implements GameObserver {
    private Scanner scanner;

    public CLIHandler() {
        this.scanner = new Scanner(System.in);
    }

    // This runs ONCE at the start
    public void onStart() {
        System.out.println("Select player:");
        System.out.println("1. Jon Snow - Warrior (300 HP, 30 ATK, 4 DEF)");
        System.out.println("2. The Hound - Warrior (400 HP, 20 ATK, 6 DEF)");
        System.out.println("3. Melisandre - Mage (100 HP, 5 ATK, 1 DEF)");
        // ... (print the rest)
    }

    public String getPlayerAction() {
        // Returns w, a, s, d, e, or q
        return scanner.nextLine();
    }

    // --- Implementing GameObserver ---

    @Override
    public void onBoardUpdate(GameBoard board) {
        System.out.println(board.toString()); // Assuming GameBoard has a toString() [cite: 301-305]
    }

    @Override
    public void onMessage(String msg) {

    }

    @Override
    public void onPlayerStats(Player player) {
        System.out.println(player.description()); // Assuming Player overrides description() [cite: 309-311]
    }

    @Override
    public void onCombat(Unit attacker, Unit defender, int attackRoll, int defenseRoll, int damage) {
        System.out.println(attacker.getName() + " engaged in combat with " + defender.getName() + ".");
        System.out.println(attacker.description());
        System.out.println(defender.description());
        System.out.println(attacker.getName() + " rolled " + attackRoll + " attack points.");
        System.out.println(defender.getName() + " rolled " + defenseRoll + " defense points.");
        System.out.println(attacker.getName() + " dealt " + damage + " damage to " + defender.getName() + ".");
    }

    @Override
    public void onLevelUp(Player player) {

    }

    @Override
    public void onAbilityCast(Player player, String abilityDescription) {

    }

    @Override
    public void onDeath(Unit unit) {

    }

    // ... Implement the other overrides (onLevelUp, onDeath, onMessage) similarly.
}