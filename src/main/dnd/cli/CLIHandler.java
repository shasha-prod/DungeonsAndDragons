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
    public void onBoardUpdate(GameBoard board) {
        // Requires GameBoard to have a toString() that builds the map string
        System.out.println(board.toString());
    }

    @Override
    public void onPlayerStats(Player player) {
        // Output format required by the assignment [cite: 317]
        System.out.println(player.description());
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
        System.out.println(player.getName() + " reached level " + player.getPlayerLevel() + ": +" + (player.getPlayerLevel() * 10) + " Health, +" + (player.getPlayerLevel() * 4) + " Attack, +" + player.getPlayerLevel() + " Defense!");
    }

    @Override
    public void onAbilityCast(Player player, String abilityDescription) {
        System.out.println(player.getName() + " cast " + abilityDescription + ".");
    }

    @Override
    public void onDeath(Unit unit) {
        System.out.println(unit.getName() + " died.");
    }

    @Override
    public void onMessage(String msg) {
        System.out.println(msg);
    }
}