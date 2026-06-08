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
import dnd.business.board.Cell;

public class GameManager {
    private List<GameObserver> observers;
    private CLIHandler cli;
    private UnitFactory factory;
    private Player player;
    private GameBoard board;
    private List<Enemy> enemies;
    private File[] levelFiles;

    public GameManager() {
        this.observers = new ArrayList<>();
        this.cli = new CLIHandler();
        this.addObserver(cli);
        this.factory = new UnitFactory();
    }

    public void addObserver(GameObserver observer) {
        this.observers.add(observer);
    }

    // --- Main Entry Point ---
    public void run(String levelDirPath) {
        File dir = new File(levelDirPath);
        if (!dir.exists() || !dir.isDirectory()) {
            cli.onMessage("Error: Invalid directory path.");
            return;
        }

        // Fetch and sort level files
        this.levelFiles = dir.listFiles((d, name) -> name.startsWith("level") && name.endsWith(".txt"));
        if (this.levelFiles != null) {
            Arrays.sort(this.levelFiles, (f1, f2) -> f1.getName().compareTo(f2.getName()));
        }

        // Initialize Player [cite: 60]
        cli.onStart();
        String choiceStr = cli.getPlayerAction();
        int choice = Integer.parseInt(choiceStr);
        this.player = factory.createPlayer(choice - 1); // UI is 1-based; array is 0-based

        // Attach UI to the player so they can announce combat/level ups
        for(GameObserver o : observers) {
            this.player.addObserver(o);
        }

        // Play through all levels sequentially [cite: 65]
        for (File levelFile : levelFiles) {
            boolean survived = playLevel(levelFile);
            if (!survived) {
                cli.onMessage("Game Over.");
                return;
            }
        }

        cli.onMessage("You win!");
    }

    // --- The Core Game Loop ---
    private boolean playLevel(File levelFile) {
        // Parse the new board
        BoardParser parser = new BoardParser(factory, player);
        this.board = parser.parseLevel(levelFile);
        this.enemies = parser.getParsedEnemies();

        // Attach UI to all newly spawned enemies
        for(Enemy e : enemies) {
            for(GameObserver o : observers) {
                e.addObserver(o);
            }
        }

        // The Tick Loop [cite: 61-64]
        while (!player.isDead() && !enemies.isEmpty()) {
            // 1. Render State
            cli.onBoardUpdate(board);
            cli.onPlayerStats(player);

            // 2. Player Turn
            String action = cli.getPlayerAction();
            processPlayerAction(action);

            // Player class-specific updates (mana, cooldowns)
            player.onGameTick();

            // 3. Enemy Turn
            for (Enemy enemy : enemies) {
                if (!enemy.isDead()) {
                    enemy.onEnemyTurn(player, board);
                }
            }

            // 4. Cleanup
            enemies.removeIf(Unit::isDead);
        }

        return !player.isDead();
    }

    // --- Action Processing ---
    private void processPlayerAction(String input) {
        Position currentPos = player.getPosition();
        Position targetPos = currentPos;

        switch (input) {
            case "w": targetPos = new Position(currentPos.getX(), currentPos.getY() - 1); break;
            case "s": targetPos = new Position(currentPos.getX(), currentPos.getY() + 1); break;
            case "a": targetPos = new Position(currentPos.getX() - 1, currentPos.getY()); break;
            case "d": targetPos = new Position(currentPos.getX() + 1, currentPos.getY()); break;
            case "e":
                player.castAbility(enemies);
                return;
            case "q":
                return; // Do nothing
            default:
                return;
        }

        // Trigger the Visitor Pattern for movement/combat via movePosition,
        // which sets player.board and player.targetPosition before dispatching.
        player.movePosition(board, targetPos);
    }
}