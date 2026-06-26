package dnd.business;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import dnd.business.board.GameBoard;
import dnd.business.factory.UnitFactory;
import dnd.business.units.Enemy;
import dnd.business.units.Player;
import dnd.cli.CLIHandler;
import dnd.business.board.Position;
import dnd.business.units.Unit;

public class GameManager {
    private List<GameObserver> observers;
    private CLIHandler cli;
    private UnitFactory factory;
    private Player player;
    private GameBoard board;
    private List<Enemy> enemies;
    private File[] levelFiles;

    public GameManager(CLIHandler cli) {
        this.observers = new ArrayList<>();
        this.cli = cli;
        this.addObserver(this.cli);
        this.factory = new UnitFactory();
    }

    public void addObserver(GameObserver observer) {
        this.observers.add(observer);
    }

    // Helper — stops repeating the nested for loop everywhere
    private void notify(String msg) {
        for (GameObserver o : observers) {
            o.onMessage(msg);
        }
    }

    private void drainUnit(Unit unit) {
        for (String msg : unit.drainMessages()) {
            notify(msg);
        }
    }

    // --- Main Entry Point ---
    public void run(String levelDirPath) {
        File dir = new File(levelDirPath);
        if (!dir.exists() || !dir.isDirectory()) {
            notify("Error: Invalid directory path.");
            return;
        }

        this.levelFiles = dir.listFiles((d, name) ->
                name.startsWith("level") && name.endsWith(".txt"));
        if (this.levelFiles != null) {
            Arrays.sort(this.levelFiles);
        }

        // Player selection — show full descriptions for all options
        cli.onStart();
        for (int i = 0; i < 7; i++) {
            notify((i + 1) + ". " + factory.createPlayer(i).description());
        }
        String choiceStr = cli.getPlayerAction();
        int choice = Integer.parseInt(choiceStr);
        this.player = factory.createPlayer(choice - 1);
        notify("You have selected:");
        notify(player.description());

        // REMOVED: no more attaching observers to player

        // Play through all levels
        for (File levelFile : levelFiles) {
            boolean survived = playLevel(levelFile);
            if (!survived) {
                notify("Game Over.");
                return;
            }
        }
        notify("You won!");
        notify("Game Over.");
    }

    // --- The Core Game Loop ---
    private boolean playLevel(File levelFile) {
        BoardParser parser = new BoardParser(factory, player);
        this.board = parser.parseLevel(levelFile);
        this.enemies = parser.getParsedEnemies();

        // REMOVED: no more attaching observers to enemies

        while (!player.isDead() && !enemies.isEmpty()) {
            // 1. Render state — through observer, not CLI directly
            notify(board.toString());
            notify(player.description());

            // 2. Player turn
            String action = cli.getPlayerAction();
            processPlayerAction(action);

            // 3. Enemy turns
            for (Enemy enemy : new ArrayList<>(enemies)) {
                if (!enemy.isDead()) {
                    enemy.onEnemyTurn(player, board);
                    drainUnit(enemy);
                    drainUnit(player); // player might have been hit
                }
            }

            // 4. Cleanup dead enemies
            enemies.removeIf(Unit::isDead);

            // 5. Player resource regen AFTER everything
            player.onGameTick();

            // 6. Check player death
            if (player.isDead()) {
                notify("You lost.");
                notify(board.toString());   // board shows X for dead player
                notify(player.description());
                return false;
            }
        }

        return true;
    }

    // --- Action Processing ---
    private void processPlayerAction(String input) {
        Position currentPos = player.getPosition();
        Position targetPos;

        switch (input) {
            case "w":
                targetPos = new Position(currentPos.getX(), currentPos.getY() - 1);
                break;
            case "s":
                targetPos = new Position(currentPos.getX(), currentPos.getY() + 1);
                break;
            case "a":
                targetPos = new Position(currentPos.getX() - 1, currentPos.getY());
                break;
            case "d":
                targetPos = new Position(currentPos.getX() + 1, currentPos.getY());
                break;
            case "e":
                player.castAbility(enemies);
                drainUnit(player);
                return;
            case "q":
                return;
            default:
                return;
        }

        player.movePosition(board, targetPos);
        drainUnit(player);
    }
}