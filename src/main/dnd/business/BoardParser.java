package dnd.business;

import dnd.business.board.*;
import dnd.business.factory.UnitFactory;
import dnd.business.units.Enemy;
import dnd.business.units.Player;
import dnd.business.units.Unit;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class BoardParser {
    private UnitFactory factory;
    private Player player;
    private List<Enemy> enemies;

    public BoardParser(UnitFactory factory, Player player) {
        this.factory = factory;
        this.player = player;
    }

    public GameBoard parseLevel(File levelFile) {
        this.enemies = new ArrayList<>();
        List<String> lines = new ArrayList<>();

        try (Scanner fileScanner = new Scanner(levelFile)) {
            while (fileScanner.hasNextLine()) {
                lines.add(fileScanner.nextLine());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // Initialize the empty board based on the file dimensions
        int width = lines.get(0).length();
        int height = lines.size();
        GameBoard board = new GameBoard(width, height);

        // Fill the board
        for (int y = 0; y < height; y++) {
            String row = lines.get(y);
            for (int x = 0; x < width; x++) {
                char tile = row.charAt(x);
                Position pos = new Position(x, y);

                if (tile == '#') {
                    board.setCell(pos, new Wall());
                } else if (tile == '.') {
                    board.setCell(pos, new Floor());
                } else if (tile == '@') {
                    Floor startFloor = new Floor();
                    player.setPosition(pos); // Move player to starting spot
                    startFloor.setCurrentOccupant(player);
                    board.setCell(pos, startFloor);
                } else {
                    // It's an enemy
                    Enemy e = factory.createEnemy(tile, pos);
                    enemies.add(e);
                    Floor enemyFloor = new Floor();
                    enemyFloor.setCurrentOccupant(e);
                    board.setCell(pos, enemyFloor);
                }
            }
        }
        return board;
    }

    public List<Enemy> getParsedEnemies() {
        return this.enemies;
    }
}