
import dnd.business.board.*;
import dnd.business.units.*;
import dnd.business.visitors.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BoardAndVisitorTest {

    private GameBoard board;

    @BeforeEach
    public void setup() {
        // Create a 10x10 board of floors with walls around the edge
        Cell[][] grid = new Cell[10][10];
        for (int y = 0; y < 10; y++) {
            for (int x = 0; x < 10; x++) {
                if (y == 0 || y == 9 || x == 0 || x == 9) {
                    grid[y][x] = new Wall();
                } else {
                    grid[y][x] = new Floor();
                }
            }
        }
        board = new GameBoard(grid);
    }

    // ===================================================================
    // GAMEBOARD BASICS
    // ===================================================================

    @Test
    public void testGetCellReturnsWallAtEdge() {
        Cell cell = board.getCell(new Position(0, 0));
        assertTrue(cell instanceof Wall);
    }

    @Test
    public void testGetCellReturnsFloorInMiddle() {
        Cell cell = board.getCell(new Position(5, 5));
        assertTrue(cell instanceof Floor);
    }

    @Test
    public void testSetAndGetOccupant() {
        Warrior w = new Warrior("Test", 100, 10, 5, 3);
        Position pos = new Position(5, 5);
        w.setPosition(pos);
        Floor floor = (Floor) board.getCell(pos);
        floor.setCurrentOccupant(w);

        assertEquals(w, floor.getCurrentOccupant());
    }

    @Test
    public void testEmptyFloorHasNullOccupant() {
        Floor floor = (Floor) board.getCell(new Position(5, 5));
        assertNull(floor.getCurrentOccupant());
    }

    // ===================================================================
    // MOVEUNIT
    // ===================================================================

    @Test
    public void testMoveUnitUpdatesPosition() {
        Warrior w = new Warrior("Test", 100, 10, 5, 3);
        Position start = new Position(3, 3);
        w.setPosition(start);
        Floor startFloor = (Floor) board.getCell(start);
        startFloor.setCurrentOccupant(w);

        Position dest = new Position(4, 3);
        board.moveUnit(w, dest);

        assertEquals(4, w.getPosition().getX());
        assertEquals(3, w.getPosition().getY());
    }

    @Test
    public void testMoveUnitClearsOldFloor() {
        Warrior w = new Warrior("Test", 100, 10, 5, 3);
        Position start = new Position(3, 3);
        w.setPosition(start);
        Floor startFloor = (Floor) board.getCell(start);
        startFloor.setCurrentOccupant(w);

        board.moveUnit(w, new Position(4, 3));

        assertNull(startFloor.getCurrentOccupant(),
            "Old floor should be empty after move");
    }

    @Test
    public void testMoveUnitSetsNewFloor() {
        Warrior w = new Warrior("Test", 100, 10, 5, 3);
        Position start = new Position(3, 3);
        w.setPosition(start);
        Floor startFloor = (Floor) board.getCell(start);
        startFloor.setCurrentOccupant(w);

        Position dest = new Position(4, 3);
        board.moveUnit(w, dest);

        Floor destFloor = (Floor) board.getCell(dest);
        assertEquals(w, destFloor.getCurrentOccupant(),
            "New floor should have the unit as occupant");
    }

    // ===================================================================
    // VISITOR PATTERN — LEVEL 1 (CellVisitor)
    // ===================================================================

    @Test
    public void testMoveIntoWallIsBlocked() {
        Warrior w = new Warrior("Test", 100, 10, 5, 3);
        Position start = new Position(1, 1);
        w.setPosition(start);
        Floor startFloor = (Floor) board.getCell(start);
        startFloor.setCurrentOccupant(w);

        // Position (0,1) is a wall
        w.movePosition(board, new Position(0, 1));

        // Player should NOT have moved
        assertEquals(1, w.getPosition().getX());
        assertEquals(1, w.getPosition().getY());
    }

    @Test
    public void testMoveIntoEmptyFloorSucceeds() {
        Warrior w = new Warrior("Test", 100, 10, 5, 3);
        Position start = new Position(1, 1);
        w.setPosition(start);
        Floor startFloor = (Floor) board.getCell(start);
        startFloor.setCurrentOccupant(w);

        w.movePosition(board, new Position(2, 1));

        assertEquals(2, w.getPosition().getX());
        assertEquals(1, w.getPosition().getY());
    }

    @Test
    public void testMoveIntoEmptyFloorClearsOldCell() {
        Warrior w = new Warrior("Test", 100, 10, 5, 3);
        Position start = new Position(1, 1);
        w.setPosition(start);
        Floor startFloor = (Floor) board.getCell(start);
        startFloor.setCurrentOccupant(w);

        w.movePosition(board, new Position(2, 1));

        assertNull(startFloor.getCurrentOccupant());
    }

    // ===================================================================
    // VISITOR PATTERN — LEVEL 2 (OccupantVisitor / Combat)
    // ===================================================================

    @Test
    public void testPlayerMovingIntoEnemyTriggersCombat() {
        Warrior player = new Warrior("Hero", 300, 30, 5, 3);
        Position playerPos = new Position(3, 3);
        player.setPosition(playerPos);
        Floor playerFloor = (Floor) board.getCell(playerPos);
        playerFloor.setCurrentOccupant(player);

        Monster enemy = new Monster('s', "Gold Cloak", 80, 8, 3, 25, 3, new Position(4, 3));
        Floor enemyFloor = (Floor) board.getCell(new Position(4, 3));
        enemyFloor.setCurrentOccupant(enemy);

        player.movePosition(board, new Position(4, 3));

        // Combat happened — check for messages
        List<String> msgs = player.drainMessages();
        assertTrue(msgs.size() > 0, "Combat should produce messages");
        boolean combatOccurred = msgs.stream().anyMatch(m ->
            m.contains("engaged") || m.contains("attacked") || m.contains("rolled"));
        assertTrue(combatOccurred, "Messages should indicate combat");
    }

    @Test
    public void testPlayerKillsEnemyAndMovesToPosition() {
        // Overpowered player to guarantee kill
        Warrior player = new Warrior("Hero", 300, 999, 5, 3);
        Position playerPos = new Position(3, 3);
        player.setPosition(playerPos);
        Floor playerFloor = (Floor) board.getCell(playerPos);
        playerFloor.setCurrentOccupant(player);

        Monster enemy = new Monster('s', "Weak", 1, 0, 0, 25, 3, new Position(4, 3));
        Floor enemyFloor = (Floor) board.getCell(new Position(4, 3));
        enemyFloor.setCurrentOccupant(enemy);

        player.movePosition(board, new Position(4, 3));

        // Enemy should be dead
        assertTrue(enemy.isDead());
        // Player should have moved to enemy's position
        assertEquals(4, player.getPosition().getX());
        // Player should have gained XP
        assertTrue(player.getExperience() > 0);
    }

    @Test
    public void testPlayerDoesNotMoveIfEnemySurvives() {
        // Weak player, strong enemy — enemy survives
        Warrior player = new Warrior("Weak", 300, 1, 5, 3);
        Position playerPos = new Position(3, 3);
        player.setPosition(playerPos);
        Floor playerFloor = (Floor) board.getCell(playerPos);
        playerFloor.setCurrentOccupant(player);

        Monster enemy = new Monster('M', "Tank", 9999, 0, 999, 500, 6, new Position(4, 3));
        Floor enemyFloor = (Floor) board.getCell(new Position(4, 3));
        enemyFloor.setCurrentOccupant(enemy);

        player.movePosition(board, new Position(4, 3));

        // Player should NOT have moved (enemy survived)
        assertEquals(3, player.getPosition().getX(),
            "Player should stay if enemy survives combat");
    }

    @Test
    public void testEnemyMovingIntoPlayerTriggersCombat() {
        Warrior player = new Warrior("Hero", 300, 30, 5, 3);
        Position playerPos = new Position(4, 3);
        player.setPosition(playerPos);
        Floor playerFloor = (Floor) board.getCell(playerPos);
        playerFloor.setCurrentOccupant(player);

        Monster enemy = new Monster('s', "Gold Cloak", 80, 8, 3, 25, 3, new Position(3, 3));
        Floor enemyFloor = (Floor) board.getCell(new Position(3, 3));
        enemyFloor.setCurrentOccupant(enemy);

        // Enemy moves right into player
        enemy.movePosition(board, new Position(4, 3));

        List<String> msgs = enemy.drainMessages();
        // Enemy attacked player — check player took damage or messages exist
        assertTrue(msgs.size() > 0 || player.getHealthAmount() < 300,
            "Enemy moving into player should trigger combat");
    }

    @Test
    public void testEnemyCannotMoveIntoAnotherEnemy() {
        Monster e1 = new Monster('s', "Enemy1", 80, 8, 3, 25, 3, new Position(3, 3));
        Floor f1 = (Floor) board.getCell(new Position(3, 3));
        f1.setCurrentOccupant(e1);

        Monster e2 = new Monster('k', "Enemy2", 200, 14, 8, 50, 4, new Position(4, 3));
        Floor f2 = (Floor) board.getCell(new Position(4, 3));
        f2.setCurrentOccupant(e2);

        // e1 tries to move right into e2
        e1.movePosition(board, new Position(4, 3));

        // e1 should NOT have moved
        assertEquals(3, e1.getPosition().getX(),
            "Enemy should not move into another enemy's cell");
    }

    // ===================================================================
    // BOARD RENDERING
    // ===================================================================

    @Test
    public void testBoardToStringContainsWallCharacter() {
        String rendered = board.toString();
        assertTrue(rendered.contains("#"));
    }

    @Test
    public void testBoardToStringContainsFloorCharacter() {
        String rendered = board.toString();
        assertTrue(rendered.contains("."));
    }

    @Test
    public void testBoardToStringShowsPlayerCharacter() {
        Warrior w = new Warrior("Test", 100, 10, 5, 3);
        Position pos = new Position(5, 5);
        w.setPosition(pos);
        Floor floor = (Floor) board.getCell(pos);
        floor.setCurrentOccupant(w);

        String rendered = board.toString();
        assertTrue(rendered.contains("@"));
    }

    @Test
    public void testBoardToStringShowsEnemyTileCharacter() {
        Monster m = new Monster('s', "Gold Cloak", 80, 8, 3, 25, 3, new Position(5, 5));
        Floor floor = (Floor) board.getCell(new Position(5, 5));
        floor.setCurrentOccupant(m);

        String rendered = board.toString();
        assertTrue(rendered.contains("s"));
    }

    @Test
    public void testInvisibleTrapShowsAsDot() {
        Trap trap = new Trap('B', "Bonus Trap", 1, 1, 1, 250, 1, 5, new Position(5, 5));
        Floor floor = (Floor) board.getCell(new Position(5, 5));
        floor.setCurrentOccupant(trap);

        Warrior player = new Warrior("Test", 100, 10, 5, 3);
        player.setPosition(new Position(0, 0));

        // Tick once to make it invisible (visibilityTime=1)
        trap.onEnemyTurn(player, board);
        trap.drainMessages();

        // Now invisible — should show as "."
        assertEquals(".", trap.toString());
    }
}
