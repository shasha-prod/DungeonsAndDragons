package dnd.business;

import dnd.business.board.*;
import dnd.business.factory.UnitFactory;
import dnd.business.units.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BossTest {

    private GameBoard board;
    private Warrior player;

    /** 10×10 all-floor board with player at (5,5). */
    @BeforeEach
    public void setUp() {
        Cell[][] grid = new Cell[10][10];
        for (int y = 0; y < 10; y++)
            for (int x = 0; x < 10; x++)
                grid[y][x] = new Floor();
        board = new GameBoard(grid);

        player = new Warrior("Jon Snow", 300, 30, 4, 3);
        player.setPosition(new Position(5, 5));
        ((Floor) board.getCell(new Position(5, 5))).setCurrentOccupant(player);
    }

    private Boss makeBoss(Position pos, int visionRange, int abilityFrequency) {
        Boss boss = new Boss('M', "The Mountain", 1000, 60, 25,
                             visionRange, abilityFrequency, 500, pos);
        ((Floor) board.getCell(pos)).setCurrentOccupant(boss);
        return boss;
    }

    // -----------------------------------------------------------------------
    // description()
    // -----------------------------------------------------------------------

    @Test
    public void testBossDescriptionFormat() {
        Boss boss = makeBoss(new Position(1, 1), 6, 6);
        String d = boss.description();
        assertTrue(d.startsWith("The Mountain\t\t"),        "Name + double-tab");
        assertTrue(d.contains("\t\tHealth: 1000/1000"),     "HP field");
        assertTrue(d.contains("\t\tAttack: 60"),            "Attack field");
        assertTrue(d.contains("\t\tDefense: 25"),           "Defense field");
        assertTrue(d.contains("\t\tExperience Value: 500"), "Exp value field");
        assertTrue(d.contains("\t\tVision Range: 6"),       "Vision range field");
        assertTrue(d.contains("\t\tAbility in: 6 ticks"),  "Ability countdown");
    }

    /**
     * After 1 in-range turn with freq=6:
     * combatTicks was 0 → 0≠6 → incremented to 1 → description shows 6-1=5 ticks.
     */
    @Test
    public void testDescriptionAbilityCountdownDecreases() {
        Boss boss = makeBoss(new Position(4, 5), 6, 6); // dist=1, in range
        boss.onEnemyTurn(player, board);
        assertTrue(boss.description().contains("Ability in: 5 ticks"),
            "Countdown should drop to 5 after 1 in-range tick");
    }

    /**
     * With freq=3 the ability fires on the 4th in-range call
     * (combatTicks: 0→1→2→3 then 3==3 → fire, reset to 0 → description=3).
     */
    @Test
    public void testDescriptionAbilityCountdownResetsAfterFire() {
        Boss boss = makeBoss(new Position(4, 5), 6, 3); // adjacent, in range
        boss.onEnemyTurn(player, board); // combatTicks → 1
        boss.onEnemyTurn(player, board); // combatTicks → 2
        boss.onEnemyTurn(player, board); // combatTicks → 3
        boss.onEnemyTurn(player, board); // 3==3 → fires, resets to 0
        boss.drainMessages();
        assertTrue(boss.description().contains("Ability in: 3 ticks"),
            "Countdown should reset to abilityFrequency after firing");
    }

    // -----------------------------------------------------------------------
    // toString
    // -----------------------------------------------------------------------

    @Test
    public void testBossToString() {
        Boss boss = new Boss('M', "The Mountain", 1000, 60, 25, 6, 6, 500, null);
        assertEquals("M", boss.toString());
    }

    // -----------------------------------------------------------------------
    // Special ability fires on the right tick
    // -----------------------------------------------------------------------

    /**
     * Algorithm: combatTicks starts at 0, fires when == freq, then resets.
     * With freq=6 the first fire happens on the 7th in-range call
     * (calls 1-6 increment combatTicks from 0 to 6; call 7 sees 6==6 → fire).
     */
    @Test
    public void testSpecialAbilityFiresAtCorrectFrequency() {
        Boss boss = makeBoss(new Position(8, 5), 9, 6); // dist=3, in range
        for (int i = 1; i <= 6; i++) {
            boss.onEnemyTurn(player, board);
            assertFalse(boss.drainMessages().stream().anyMatch(m -> m.contains("shoots")),
                "Should NOT fire on call " + i);
        }
        boss.onEnemyTurn(player, board); // combatTicks==6 → fire
        assertTrue(boss.drainMessages().stream().anyMatch(m -> m.contains("shoots")),
            "Should fire special ability on 7th in-range call");
    }

    /**
     * With freq=3 the first fire is on call 4, the second on call 8
     * (each cycle: 3 increment calls then 1 fire call).
     */
    @Test
    public void testSpecialAbilityFiresEveryNthTick() {
        Boss boss = makeBoss(new Position(8, 5), 9, 3); // dist=3, in range
        // First cycle: 3 non-fire ticks
        boss.onEnemyTurn(player, board);
        boss.onEnemyTurn(player, board);
        boss.onEnemyTurn(player, board);
        boss.drainMessages();
        // First fire (call 4)
        boss.onEnemyTurn(player, board);
        assertTrue(boss.drainMessages().stream().anyMatch(m -> m.contains("shoots")),
            "Should fire at call 4 (freq=3)");
        // Second cycle: 3 non-fire ticks
        boss.onEnemyTurn(player, board);
        boss.onEnemyTurn(player, board);
        boss.onEnemyTurn(player, board);
        boss.drainMessages();
        // Second fire (call 8)
        boss.onEnemyTurn(player, board);
        assertTrue(boss.drainMessages().stream().anyMatch(m -> m.contains("shoots")),
            "Should fire again at call 8 (second cycle)");
    }

    // -----------------------------------------------------------------------
    // Special ability message format (matches reference log)
    // -----------------------------------------------------------------------

    /**
     * With freq=1: call 1 increments to combatTicks=1 (move, no fire).
     * Call 2: 1==1 → fire. Drain call-2 messages and check format.
     */
    @Test
    public void testSpecialAbilityMessageFormat() {
        Boss boss = makeBoss(new Position(8, 5), 9, 1); // dist=3, in range
        boss.onEnemyTurn(player, board); // call 1: combatTicks=1, move (no fire)
        boss.drainMessages();
        boss.onEnemyTurn(player, board); // call 2: 1==1 → fire
        List<String> msgs = boss.drainMessages();

        // "X shoots Y for N damage" (no trailing period)
        assertTrue(msgs.stream().anyMatch(m ->
                m.equals("The Mountain shoots Jon Snow for 60 damage")),
            "Shoot message must be exact (no period). Got: " + msgs);

        // "Y rolled N defense points."
        assertTrue(msgs.stream().anyMatch(m ->
                m.matches("Jon Snow rolled \\d+ defense points\\.")),
            "Defense roll message format. Got: " + msgs);

        // "X hit Y for N ability damage."
        assertTrue(msgs.stream().anyMatch(m ->
                m.matches("The Mountain hit Jon Snow for \\d+ ability damage\\.")),
            "Hit message format. Got: " + msgs);
    }

    /**
     * Player with enormous defense should take 0 ability damage.
     * Boss is far (dist>1) so call 1 is a move (no melee); call 2 fires the ability.
     */
    @Test
    public void testSpecialAbilityDamageIsReducedByPlayerDefense() {
        Warrior tankPlayer = new Warrior("Tank", 300, 30, 9999, 3);
        tankPlayer.setPosition(new Position(3, 3));
        ((Floor) board.getCell(new Position(3, 3))).setCurrentOccupant(tankPlayer);

        Boss boss = makeBoss(new Position(8, 5), 9, 1); // freq=1, dist far → call 2 fires
        boss.onEnemyTurn(tankPlayer, board); // call 1: move
        boss.drainMessages();
        boss.onEnemyTurn(tankPlayer, board); // call 2: fire
        List<String> msgs = boss.drainMessages();

        assertTrue(msgs.stream().anyMatch(m -> m.contains("for 0 ability damage.")),
            "Tank player should absorb all damage. Got: " + msgs);
        assertEquals(300, tankPlayer.getHealthAmount(), "HP should not decrease");
    }

    /**
     * Fragile player (1 HP, 0 defense) should die when the ability fires.
     * Boss is far (dist=3) so call 1 moves the boss — no melee — fragile survives.
     * Call 2 fires the ability and kills the fragile player.
     */
    @Test
    public void testSpecialAbilityKillsPlayerMessage() {
        Warrior fragile = new Warrior("Fragile", 1, 30, 0, 3);
        fragile.setPosition(new Position(5, 5));
        ((Floor) board.getCell(new Position(5, 5))).setCurrentOccupant(fragile);

        // Place boss far enough that call-1 is a move (not melee)
        Boss boss = makeBoss(new Position(8, 5), 9, 1); // dist=3 from (5,5)
        boss.onEnemyTurn(fragile, board); // call 1: combatTicks=1, move → no melee
        boss.drainMessages();
        boss.onEnemyTurn(fragile, board); // call 2: 1==1 → fire, fragile dies
        List<String> msgs = boss.drainMessages();

        assertTrue(fragile.isDead(), "Fragile player should be dead");
        assertTrue(msgs.stream().anyMatch(m ->
                m.equals("Fragile was killed by The Mountain.")),
            "Death message must be present. Got: " + msgs);
    }

    // -----------------------------------------------------------------------
    // Special ability does NOT fire when out of vision range
    // -----------------------------------------------------------------------

    /**
     * When the player is outside visionRange the boss does random movement
     * and combatTicks stays at 0. The ability must never fire in this scenario.
     */
    @Test
    public void testSpecialAbilityDoesNotFireWhenOutOfVisionRange() {
        // visionRange=1 → dist < 1 is impossible (can never be on same tile as player)
        Boss boss = makeBoss(new Position(1, 5), 1, 1);
        for (int i = 0; i < 10; i++) {
            boss.onEnemyTurn(player, board);
            assertFalse(boss.drainMessages().stream().anyMatch(m -> m.contains("shoots")),
                "Ability must not fire when out of vision range (call " + (i + 1) + ")");
        }
    }

    // -----------------------------------------------------------------------
    // Regular melee on non-special ticks
    // -----------------------------------------------------------------------

    @Test
    public void testRegularMeleeWhenAdjacentAndNoSpecial() {
        // Adjacent boss, freq=6 → tick 1 is NOT a special tick
        Boss boss = makeBoss(new Position(4, 5), 6, 6);
        boss.onEnemyTurn(player, board);
        List<String> msgs = boss.drainMessages();

        assertFalse(msgs.stream().anyMatch(m -> m.contains("shoots")),
            "Call 1 should NOT fire special");
        assertTrue(msgs.stream().anyMatch(m ->
                m.equals("The Mountain engaged in combat with Jon Snow.")),
            "Should produce regular combat header on non-special tick");
    }

    /**
     * With freq=1 the first call increments combatTicks to 1 (no fire).
     * The second call fires the special — no melee header should appear.
     */
    @Test
    public void testNoRegularMeleeOnSpecialTick() {
        Boss boss = makeBoss(new Position(4, 5), 6, 1); // adjacent, in range
        boss.onEnemyTurn(player, board); // call 1: ticks=0→1, melee
        boss.drainMessages();
        boss.onEnemyTurn(player, board); // call 2: ticks==1 → fire special
        List<String> msgs = boss.drainMessages();

        assertTrue(msgs.stream().anyMatch(m -> m.contains("shoots")),
            "Call 2 should fire special");
        assertFalse(msgs.stream().anyMatch(m -> m.contains("engaged in combat")),
            "Should NOT produce regular combat header on special tick");
    }

    // -----------------------------------------------------------------------
    // Movement
    // -----------------------------------------------------------------------

    @Test
    public void testBossMovesWhenInVisionRange() {
        // Boss at (2,5), player at (5,5), dist=3, visionRange=9
        Boss boss = makeBoss(new Position(2, 5), 9, 6);
        boss.onEnemyTurn(player, board);
        boss.drainMessages();
        Position pos = boss.getPosition();
        // Boss should have moved one step closer (x from 2 toward 5)
        assertEquals(3, pos.getX(), "Boss should have stepped toward player (x+1)");
        assertEquals(5, pos.getY(), "Y should be unchanged");
    }

    /**
     * When out of vision range combatTicks must stay at 0 (resets every tick).
     */
    @Test
    public void testCombatTicksStayZeroWhenOutOfVisionRange() {
        // visionRange=1 → dist < 1 is impossible, boss always in out-of-range mode
        Boss boss = makeBoss(new Position(1, 5), 1, 6);
        boss.onEnemyTurn(player, board);
        boss.drainMessages();
        assertEquals(0, boss.getCombatTicks(),
            "combatTicks should stay 0 when out of vision range");
    }

    @Test
    public void testBossMovesHorizontallyTowardPlayer() {
        // Boss at (2,5), player at (5,5) — same row, should step right to (3,5)
        Boss boss = makeBoss(new Position(2, 5), 9, 6);
        boss.onEnemyTurn(player, board);
        boss.drainMessages();
        Position pos = boss.getPosition();
        assertEquals(3, pos.getX(), "Boss x should increase by 1");
        assertEquals(5, pos.getY(), "Boss y should stay the same");
    }

    // -----------------------------------------------------------------------
    // UnitFactory wires M, C, K to Boss with correct parameters
    // -----------------------------------------------------------------------

    @Test
    public void testUnitFactoryCreatesMountainAsBoss() {
        UnitFactory factory = new UnitFactory();
        Enemy e = factory.createEnemy('M', new Position(0, 0));
        assertInstanceOf(Boss.class, e, "'M' tile should create a Boss");
        assertEquals("The Mountain", e.getName());
        assertEquals(1000, e.getHealthAmount());
        assertEquals(500, e.getExperienceValue());
        Boss boss = (Boss) e;
        assertEquals(5, boss.getAbilityFrequency(), "Mountain abilityFrequency must be 5");
    }

    @Test
    public void testUnitFactoryCreatesQueenCerseiAsBoss() {
        UnitFactory factory = new UnitFactory();
        Enemy e = factory.createEnemy('C', new Position(0, 0));
        assertInstanceOf(Boss.class, e, "'C' tile should create a Boss");
        assertEquals("Queen Cersei", e.getName());
        assertEquals(8, ((Boss) e).getAbilityFrequency(), "Cersei abilityFrequency must be 8");
    }

    @Test
    public void testUnitFactoryCreatesNightsKingAsBoss() {
        UnitFactory factory = new UnitFactory();
        Enemy e = factory.createEnemy('K', new Position(0, 0));
        assertInstanceOf(Boss.class, e, "'K' tile should create a Boss");
        assertEquals("Night's King", e.getName());
        assertEquals(3, ((Boss) e).getAbilityFrequency(), "Night's King abilityFrequency must be 3");
    }

    // -----------------------------------------------------------------------
    // Combat ticks accessor
    // -----------------------------------------------------------------------

    @Test
    public void testCombatTicksIncrementsEachTurnInRange() {
        Boss boss = makeBoss(new Position(8, 5), 9, 100); // in range, freq=100 → never fires
        assertEquals(0, boss.getCombatTicks());
        boss.onEnemyTurn(player, board);
        boss.drainMessages();
        assertEquals(1, boss.getCombatTicks());
        boss.onEnemyTurn(player, board);
        boss.drainMessages();
        assertEquals(2, boss.getCombatTicks());
    }
}
