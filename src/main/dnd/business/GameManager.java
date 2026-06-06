package dnd.business;

import dnd.business.GameObserver;
import dnd.business.board.GameBoard;
import dnd.business.factory.UnitFactory;
import dnd.business.units.Enemy;
import dnd.business.units.Player;
import dnd.cli.CLIHandler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GameManager {
    private List<GameObserver> observers;
    private CLIHandler cli;
    private UnitFactory factory;
    private Player player;
    private GameBoard board;
    private List<Enemy> enemies;
    private File[] levelFiles;

    public GameManager() {
        observers = new ArrayList<>();
        cli = new CLIHandler();
        addObserver(cli); // Wire up the UI!
        factory = new UnitFactory();
    }

    public void addObserver(GameObserver observer) {
        observers.add(observer);
    }

    // The Main Entry Point
    public void run(String levelDirPath) {

    }

    private File[] getLevelFilesFromDir(String levelDirPath) {
    }

    // The Loop
    private boolean playLevel(File levelFile) {
        // 1. Parse the board
        BoardParser parser = new BoardParser(factory, player);
        this.board = parser.parseLevel(levelFile);
        this.enemies = parser.getParsedEnemies();

        // 2. The Game Tick Loop [cite: 61-64]
        while (!player.isDead() && !enemies.isEmpty()) {
            // Render UI
            for (GameObserver obs : observers) {
                obs.onBoardUpdate(board);
                obs.onPlayerStats(player);
            }

            // Player Turn
            String action = cli.getPlayerAction();
            processPlayerAction(action);
            player.onGameTick(); // For cooldowns/mana [cite: 141-144, 171-172]

            // Enemy Turn
            for (Enemy e : enemies) {
                if (!e.isDead()) {
                    e.onEnemyTurn(player, board);
                }
            }

            // Cleanup dead enemies
            removeDeadEnemies();
        }

        return !player.isDead();
    }

    private void processPlayerAction(String input) {
        // w,a,s,d -> Ask player to use visitor pattern to move
        // e -> player.castAbility();
        // q -> do nothing
    }

    private void removeDeadEnemies() {
        enemies.removeIf(Enemy::isDead);
    }
}