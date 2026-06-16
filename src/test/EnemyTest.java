package dnd.business;

import dnd.business.board.*;
import dnd.business.units.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class EnemyTest {

    // ===================================================================
    // TRAP VISIBILITY CYCLING
    // ===================================================================

    // Using Bonus Trap: visibilityTime=1, invisibilityTime=5
    private Trap createBonusTrap(int x, int y) {
        return new Trap('B', "Bonus Trap", 1, 1, 1, 250, 1, 5, new Position(x, y));
    }

    // Using Queen's Trap: visibilityTime=3, invisibilityTime=7
    private Trap createQueensTrap(int x, int y) {
        return new Trap('Q', "Queen's Trap", 250, 50, 10, 100, 3, 7, new Position(x, y));
    }

    private GameBoard createSimpleBoard() {
        Cell[][] grid = new Cell[10][10];
        for (int y = 0; y < 10; y++) {
            for (int x = 0; x < 10; x++) {
                grid[y][x] = new Floor();
            }
        }
        return new GameBoard(grid);
    }

    @Test
    public void testTrapStartsVisible() {
        Trap trap = createBonusTrap(5, 5);
        assertEquals("B", trap.toString());
    }

    @Test
    public void testBonusTrapVisibilityCycle() {
        // visibilityTime=1, invisibilityTime=5
        // Spec pseudocode: visible = ticksCount < visibilityTime
        // Cycle: tick0=T, tick1=F, tick2=F, tick3=F, tick4=F, tick5=F(reset), tick6=T...
        Trap trap = createBonusTrap(5, 5);
        GameBoard board = createSimpleBoard();
        Warrior player = new Warrior("Test", 100, 10, 5, 3);
        player.setPosition(new Position(0, 0)); // far away, no combat

        // Tick 0: visible (0 < 1 = true)
        assertEquals("B", trap.toString());

        trap.onEnemyTurn(player, board); // processes tick 0
        // After tick 0: ticksCount becomes 1
        // visible = 1 < 1 = false
        assertEquals(".", trap.toString());

        trap.onEnemyTurn(player, board); // tick 1: ticksCount=1→2
        assertEquals(".", trap.toString()); // 2 < 1 = false

        trap.onEnemyTurn(player, board); // tick 2: ticksCount=2→3
        assertEquals(".", trap.toString());

        trap.onEnemyTurn(player, board); // tick 3: ticksCount=3→4
        assertEquals(".", trap.toString());

        trap.onEnemyTurn(player, board); // tick 4: ticksCount=4→5
        assertEquals(".", trap.toString());

        trap.onEnemyTurn(player, board); // tick 5: ticksCount=5, 5==(1+5) → reset to 0
        // visible = 0 < 1 = true
        assertEquals("B", trap.toString()); // visible again!
    }

    @Test
    public void testQueensTrapVisibilityCycle() {
        // visibilityTime=3, invisibilityTime=7
        Trap trap = createQueensTrap(5, 5);
        GameBoard board = createSimpleBoard();
        Warrior player = new Warrior("Test", 100, 10, 5, 3);
        player.setPosition(new Position(0, 0));

        // First 3 ticks should be visible
        for (int i = 0; i < 3; i++) {
            assertEquals("Q", trap.toString(), "Should be visible at tick " + i);
            trap.onEnemyTurn(player, board);
            trap.drainMessages();
        }

        // Next ticks should be invisible (until reset at tick 10)
        for (int i = 3; i < 10; i++) {
            assertEquals(".", trap.toString(), "Should be invisible at tick " + i);
            trap.onEnemyTurn(player, board);
            trap.drainMessages();
        }

        // After tick 10 (reset), should be visible again
        assertEquals("Q", trap.toString(), "Should be visible after full cycle");
    }

    @Test
    public void testTrapAttacksPlayerInRange() {
        Trap trap = createQueensTrap(5, 5); // atk=50
        GameBoard board = createSimpleBoard();
        Warrior player = new Warrior("Test", 300, 10, 5, 3);
        player.setPosition(new Position(5, 6)); // range 1 < 2

        int healthBefore = player.getHealthAmount();
        trap.onEnemyTurn(player, board);

        List<String> msgs = trap.drainMessages();
        // Trap should have attacked
        assertTrue(msgs.size() > 0 || player.getHealthAmount() < healthBefore,
            "Trap should attack player within range 2");
    }

    @Test
    public void testTrapDoesNotAttackPlayerOutOfRange() {
        Trap trap = createQueensTrap(5, 5);
        GameBoard board = createSimpleBoard();
        Warrior player = new Warrior("Test", 300, 10, 5, 3);
        player.setPosition(new Position(5, 8)); // range 3, NOT < 2

        trap.onEnemyTurn(player, board);
        List<String> msgs = trap.drainMessages();
        assertEquals(0, msgs.size(), "Trap should not attack player outside range 2");
    }

    @Test
    public void testTrapDoesNotMove() {
        Trap trap = createQueensTrap(5, 5);
        GameBoard board = createSimpleBoard();
        Warrior player = new Warrior("Test", 300, 10, 5, 3);
        player.setPosition(new Position(0, 0));

        for (int i = 0; i < 20; i++) {
            trap.onEnemyTurn(player, board);
            trap.drainMessages();
        }

        assertEquals(5, trap.getPosition().getX());
        assertEquals(5, trap.getPosition().getY());
    }

    // ===================================================================
    // MONSTER MOVEMENT AI
    // ===================================================================

    private Monster createMonster(int x, int y) {
        // visionRange=3
        return new Monster('s', "Gold Cloak", 80, 8, 3, 25, 3, new Position(x, y));
    }

    @Test
    public void testMonsterChasesPlayerWhenInVisionRange() {
        // Monster at (5,5), Player at (5,3) → distance 2 < 3 (vision range)
        // dy = 5-3 = 2, dx = 0. |dx|<|dy|, dy>0 → move up (y-1)
        Monster monster = createMonster(5, 5);
        GameBoard board = createSimpleBoard();
        Floor monsterFloor = (Floor) board.getCell(new Position(5, 5));
        monsterFloor.setCurrentOccupant(monster);

        Warrior player = new Warrior("Test", 300, 10, 5, 3);
        player.setPosition(new Position(5, 3));

        monster.onEnemyTurn(player, board);
        monster.drainMessages();

        // Monster should have moved up: y=5 → y=4
        assertEquals(4, monster.getPosition().getY(),
            "Monster should chase player upward");
    }

    @Test
    public void testMonsterMovesHorizontallyWhenDxGreater() {
        // Monster at (8,5), Player at (5,5) → dx=3, dy=0. |dx|>|dy|, dx>0 → move left
        Monster monster = createMonster(8, 5);
        GameBoard board = createSimpleBoard();
        Floor monsterFloor = (Floor) board.getCell(new Position(8, 5));
        monsterFloor.setCurrentOccupant(monster);

        Warrior player = new Warrior("Test", 300, 10, 5, 3);
        player.setPosition(new Position(5, 5));

        monster.onEnemyTurn(player, board);
        monster.drainMessages();

        // Monster should move left: x=8 → x=7
        assertEquals(7, monster.getPosition().getX(),
            "Monster should chase player to the left");
    }

    @Test
    public void testMonsterRandomMovesWhenOutOfVisionRange() {
        // Monster at (5,5), Player at (50,50) → way out of vision range 3
        // Should make random move (could be any direction or stay)
        Monster monster = createMonster(5, 5);
        GameBoard board = createSimpleBoard();
        Floor monsterFloor = (Floor) board.getCell(new Position(5, 5));
        monsterFloor.setCurrentOccupant(monster);

        Warrior player = new Warrior("Test", 300, 10, 5, 3);
        player.setPosition(new Position(50, 50));

        // Run many times — position should change at least once (4/5 chance per tick)
        boolean moved = false;
        for (int i = 0; i < 20; i++) {
            Position before = monster.getPosition();
            monster.onEnemyTurn(player, board);
            monster.drainMessages();
            if (monster.getPosition().getX() != before.getX() ||
                monster.getPosition().getY() != before.getY()) {
                moved = true;
                break;
            }
        }
        assertTrue(moved, "Monster should eventually move randomly when player is out of range");
    }

    @Test
    public void testMonsterDoesNotMoveThroughWalls() {
        // Create board with wall next to monster
        Cell[][] grid = new Cell[5][5];
        for (int y = 0; y < 5; y++) {
            for (int x = 0; x < 5; x++) {
                grid[y][x] = new Floor();
            }
        }
        grid[2][1] = new Wall(); // wall to the left of monster

        GameBoard board = new GameBoard(grid);
        Monster monster = createMonster(2, 2);
        Floor monsterFloor = (Floor) board.getCell(new Position(2, 2));
        monsterFloor.setCurrentOccupant(monster);

        Warrior player = new Warrior("Test", 300, 10, 5, 3);
        // Player to the left — monster wants to go left but wall blocks
        player.setPosition(new Position(0, 2));

        monster.onEnemyTurn(player, board);
        monster.drainMessages();

        // Monster should NOT be at (1,2) because that's a wall
        assertFalse(monster.getPosition().getX() == 1 && monster.getPosition().getY() == 2,
            "Monster should not move through walls");
    }
}
