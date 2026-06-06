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
        // 1. Get files (Assuming helper method to read directory)
        levelFiles = getLevelFilesFromDir(levelDirPath);

        // 2. Start UI and get player
        cli.onStart();
        String choice = cli.getPlayerAction();
        player = factory.createPlayer(Integer.parseInt(choice));

        // Hook the player up to the observers so it can report combat/level ups
        for(GameObserver o : observers) player.addObserver(o);

        // 3. Play through the files
        for (int i = 0; i < levelFiles.length; i++) {
            boolean playerSurvived = playLevel(levelFiles[i]);
            if (!playerSurvived) {
                break; // Game Over
            }
        }

        if (!player.isDead()) {
            cli.onMessage("You win!");
        }
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