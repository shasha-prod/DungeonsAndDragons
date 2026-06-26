package dnd.business;

import dnd.business.board.*;
import dnd.business.units.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests that every message the game produces has the exact right string.
 * We test exact substrings (contains) rather than full lines so that
 * random roll numbers don't break the tests.
 */
public class MessageFormatTest {

    // ===================================================================
    // description() — exact field format for every class
    // ===================================================================

    @Test
    public void testWarriorDescription() {
        Warrior w = new Warrior("Jon Snow", 300, 30, 4, 3);
        String d = w.description();
        // Each field separated by \t\t
        assertTrue(d.startsWith("Jon Snow\t\t"),          "Name + double-tab");
        assertTrue(d.contains("\t\tHealth: 300/300"),     "HP field");
        assertTrue(d.contains("\t\tAttack: 30"),          "Attack field");
        assertTrue(d.contains("\t\tDefense: 4"),          "Defense field (American spelling)");
        assertTrue(d.contains("\t\tLevel: 1"),            "Level field");
        assertTrue(d.contains("\t\tExperience: 0/50"),    "Experience field");
        assertTrue(d.contains("\t\tCooldown: 0/3"),       "Cooldown field");
    }

    @Test
    public void testMageDescription() {
        Mage m = new Mage("Melisandre", 100, 5, 1, 300, 30, 15, 5, 6);
        String d = m.description();
        assertTrue(d.startsWith("Melisandre\t\t"));
        assertTrue(d.contains("\t\tHealth: 100/100"));
        assertTrue(d.contains("\t\tAttack: 5"));
        assertTrue(d.contains("\t\tDefense: 1"));
        assertTrue(d.contains("\t\tLevel: 1"));
        assertTrue(d.contains("\t\tExperience: 0/50"));
        assertTrue(d.contains("\t\tMana: 75/300"),      "currentMana/manaPool (not manaCost)");
        assertTrue(d.contains("\t\tSpell Power: 15"));
    }

    @Test
    public void testRogueDescription() {
        Rogue r = new Rogue("Arya Stark", 150, 40, 2, 20);
        String d = r.description();
        assertTrue(d.startsWith("Arya Stark\t\t"));
        assertTrue(d.contains("\t\tHealth: 150/150"));
        assertTrue(d.contains("\t\tAttack: 40"));
        assertTrue(d.contains("\t\tDefense: 2"));
        assertTrue(d.contains("\t\tLevel: 1"));
        assertTrue(d.contains("\t\tExperience: 0/50"));
        assertTrue(d.contains("\t\tEnergy: 100/100"));
    }

    @Test
    public void testHunterDescription() {
        Hunter h = new Hunter("Ygritte", 220, 30, 2, 6);
        String d = h.description();
        assertTrue(d.startsWith("Ygritte\t\t"));
        assertTrue(d.contains("\t\tHealth: 220/220"));
        assertTrue(d.contains("\t\tAttack: 30"));
        assertTrue(d.contains("\t\tDefense: 2"));
        assertTrue(d.contains("\t\tLevel: 1"));
        assertTrue(d.contains("\t\tExperience: 0/50"));
        assertTrue(d.contains("\t\tArrows: 10"));
        assertTrue(d.contains("\t\tRange: 6"));
    }

    @Test
    public void testMonsterDescription() {
        // Constructor: (tile, name, hp, atk, def, visionRange, experienceValue, pos)
        Monster m = new Monster('s', "Gold Cloak", 80, 8, 3, 3, 25, new Position(0, 0));
        String d = m.description();
        assertTrue(d.startsWith("Gold Cloak\t\t"));
        assertTrue(d.contains("\t\tHealth: 80/80"));
        assertTrue(d.contains("\t\tAttack: 8"));
        assertTrue(d.contains("\t\tDefense: 3"));
        assertTrue(d.contains("\t\tExperience Value: 25"));
        assertTrue(d.contains("\t\tVision Range: 3"));
        // Monster has no Level or Experience field
        assertFalse(d.contains("Level:"),      "Monster should not have Level field");
        assertFalse(d.contains("Experience:"), "Monster should not have Experience: field");
    }

    @Test
    public void testDescriptionUsesTabsNotSpaces() {
        Warrior w = new Warrior("Jon Snow", 300, 30, 4, 3);
        String d = w.description();
        // The separator must be \t\t, not spaces
        assertFalse(d.contains("Health:  "),   "Should use tabs, not spaces before Health");
        assertFalse(d.contains("  Health:"),   "Should use tabs, not spaces before Health");
        assertTrue(d.contains("\t\tHealth:"),  "Must use double-tab before Health");
    }

    @Test
    public void testDescriptionSpellsDefenseCorrectly() {
        // American English: "Defense" not "Defence"
        Warrior w = new Warrior("Jon Snow", 300, 30, 4, 3);
        assertFalse(w.description().contains("Defence"), "Must spell Defense (American)");
        assertTrue(w.description().contains("Defense"),  "Must spell Defense (American)");
    }

    // ===================================================================
    // description() reflects current (mutable) state
    // ===================================================================

    @Test
    public void testWarriorDescriptionReflectsDamage() {
        Warrior w = new Warrior("Jon Snow", 300, 30, 4, 3);
        w.takeDamage(50);
        String d = w.description();
        assertTrue(d.contains("\t\tHealth: 250/300"), "Should show current/max HP after damage");
    }

    @Test
    public void testMageDescriptionReflectsManaCost() {
        Mage m = new Mage("Melisandre", 100, 5, 1, 300, 30, 15, 5, 6);
        m.setPosition(new Position(5, 5));
        List<Enemy> enemies = new ArrayList<>();
        enemies.add(new Monster('s', "Gold Cloak", 80, 8, 3, 25, 3, new Position(5, 6)));
        m.castAbility(enemies);
        m.drainMessages();
        // Mana went from 75 to 45
        assertTrue(m.description().contains("\t\tMana: 45/300"),
            "Mana should reflect after spending");
    }

    @Test
    public void testRogueDescriptionReflectsEnergyAfterCast() {
        Rogue r = new Rogue("Arya Stark", 150, 40, 2, 20);
        r.setPosition(new Position(5, 5));
        List<Enemy> enemies = new ArrayList<>();
        enemies.add(new Monster('s', "Gold Cloak", 80, 8, 3, 25, 3, new Position(5, 6)));
        r.castAbility(enemies);
        r.drainMessages();
        assertTrue(r.description().contains("\t\tEnergy: 80/100"),
            "Energy should reflect 100 - 20 = 80 after one cast");
    }

    @Test
    public void testHunterDescriptionReflectsArrowsAfterShot() {
        Hunter h = new Hunter("Ygritte", 220, 30, 2, 6);
        h.setPosition(new Position(5, 5));
        List<Enemy> enemies = new ArrayList<>();
        enemies.add(new Monster('s', "Gold Cloak", 9999, 8, 3, 25, 3, new Position(5, 6)));
        h.castAbility(enemies);
        h.drainMessages();
        assertTrue(h.description().contains("\t\tArrows: 9"),
            "Arrow count should be 10 - 1 = 9 after one shot");
    }

    // ===================================================================
    // Combat message sequence — 6 lines in order
    // ===================================================================

    @Test
    public void testCombatMessageSequence_PlayerAttacksEnemy() {
        Warrior player = new Warrior("Jon Snow", 300, 30, 4, 3);
        player.setPosition(new Position(3, 3));

        Cell[][] grid = new Cell[10][10];
        for (int y = 0; y < 10; y++)
            for (int x = 0; x < 10; x++)
                grid[y][x] = new Floor();
        GameBoard board = new GameBoard(grid);

        Floor pFloor = (Floor) board.getCell(new Position(3, 3));
        pFloor.setCurrentOccupant(player);

        Monster enemy = new Monster('s', "Gold Cloak", 9999, 8, 3, 25, 3, new Position(4, 3));
        Floor eFloor = (Floor) board.getCell(new Position(4, 3));
        eFloor.setCurrentOccupant(enemy);

        player.movePosition(board, new Position(4, 3));
        List<String> msgs = player.drainMessages();

        // Must have at least 6 messages
        assertTrue(msgs.size() >= 6, "Combat should produce at least 6 messages");

        // Line 0: "X engaged in combat with Y."
        assertEquals("Jon Snow engaged in combat with Gold Cloak.", msgs.get(0));

        // Line 1: player description
        assertTrue(msgs.get(1).startsWith("Jon Snow\t\t"),
            "Second message should be player description");

        // Line 2: enemy description
        assertTrue(msgs.get(2).startsWith("Gold Cloak\t\t"),
            "Third message should be enemy description");

        // Line 3: attacker rolled N attack points
        assertTrue(msgs.get(3).matches("Jon Snow rolled \\d+ attack points\\."),
            "Fourth message: 'Jon Snow rolled N attack points.' — got: " + msgs.get(3));

        // Line 4: defender rolled N defense points
        assertTrue(msgs.get(4).matches("Gold Cloak rolled \\d+ defense points\\."),
            "Fifth message: 'Gold Cloak rolled N defense points.' — got: " + msgs.get(4));

        // Line 5: damage dealt
        assertTrue(msgs.get(5).matches("Jon Snow dealt \\d+ damage to Gold Cloak\\."),
            "Sixth message: 'Jon Snow dealt N damage to Gold Cloak.' — got: " + msgs.get(5));
    }

    @Test
    public void testCombatMessageSequence_EnemyAttacksPlayer() {
        Warrior player = new Warrior("Jon Snow", 300, 30, 4, 3);
        player.setPosition(new Position(4, 3));

        Cell[][] grid = new Cell[10][10];
        for (int y = 0; y < 10; y++)
            for (int x = 0; x < 10; x++)
                grid[y][x] = new Floor();
        GameBoard board = new GameBoard(grid);
        ((Floor) board.getCell(new Position(4, 3))).setCurrentOccupant(player);

        Monster enemy = new Monster('s', "Gold Cloak", 80, 8, 3, 25, 3, new Position(3, 3));
        ((Floor) board.getCell(new Position(3, 3))).setCurrentOccupant(enemy);

        enemy.onEnemyTurn(player, board);
        List<String> msgs = enemy.drainMessages();

        assertTrue(msgs.size() >= 6, "Enemy combat should produce at least 6 messages");
        assertEquals("Gold Cloak engaged in combat with Jon Snow.", msgs.get(0));
        assertTrue(msgs.get(1).startsWith("Gold Cloak\t\t"));
        assertTrue(msgs.get(2).startsWith("Jon Snow\t\t"));
        assertTrue(msgs.get(3).matches("Gold Cloak rolled \\d+ attack points\\."));
        assertTrue(msgs.get(4).matches("Jon Snow rolled \\d+ defense points\\."));
        assertTrue(msgs.get(5).matches("Gold Cloak dealt \\d+ damage to Jon Snow\\."));
    }

    @Test
    public void testPlayerKillsEnemyDeathMessage() {
        // Overpowered player guarantees kill
        Warrior player = new Warrior("Jon Snow", 300, 9999, 4, 3);
        player.setPosition(new Position(3, 3));

        Cell[][] grid = new Cell[10][10];
        for (int y = 0; y < 10; y++)
            for (int x = 0; x < 10; x++)
                grid[y][x] = new Floor();
        GameBoard board = new GameBoard(grid);
        ((Floor) board.getCell(new Position(3, 3))).setCurrentOccupant(player);

        // Constructor: (tile, name, hp, atk, def, visionRange, experienceValue, pos)
        Monster enemy = new Monster('s', "Gold Cloak", 1, 0, 0, 3, 25, new Position(4, 3));
        ((Floor) board.getCell(new Position(4, 3))).setCurrentOccupant(enemy);

        player.movePosition(board, new Position(4, 3));
        List<String> msgs = player.drainMessages();

        boolean hasDeath = msgs.stream().anyMatch(m ->
            m.equals("Gold Cloak died. Jon Snow gained 25 experience"));
        assertTrue(hasDeath, "Should contain exact death/experience message. Got: " + msgs);
    }

    // ===================================================================
    // Level-up messages — base line + class-specific line
    // ===================================================================

    @Test
    public void testWarriorLevelUpMessages() {
        Warrior w = new Warrior("Jon Snow", 300, 30, 4, 3);
        w.addExperience(50); // level up to 2
        List<String> msgs = w.drainMessages();

        // Warrior shows ONE combined line: base + warrior bonus stats
        // HP: 10*2 + 5*2 = 30, ATK: 4*2 + 2*2 = 12, DEF: 1*2 + 1*2 = 4
        boolean hasBase = msgs.stream().anyMatch(m ->
            m.equals("Jon Snow reached level 2: +30 Health, +12 Attack, +4 Defense"));
        assertTrue(hasBase, "Base level-up line must be exact. Got: " + msgs);
    }

    @Test
    public void testMageLevelUpMessages() {
        Mage m = new Mage("Melisandre", 100, 5, 1, 300, 30, 15, 5, 6);
        m.addExperience(50);
        List<String> msgs = m.drainMessages();

        boolean hasBase = msgs.stream().anyMatch(msg ->
            msg.equals("Melisandre reached level 2: +20 Health, +8 Attack, +2 Defense"));
        assertTrue(hasBase, "Base level-up line. Got: " + msgs);

        boolean hasBonus = msgs.stream().anyMatch(msg ->
            msg.equals("                +50 maximum mana, +20 spell power"));
        assertTrue(hasBonus, "Mage bonus line (25*2=50 mana, 10*2=20 sp). Got: " + msgs);
    }

    @Test
    public void testRogueLevelUpMessages() {
        Rogue r = new Rogue("Arya Stark", 150, 40, 2, 20);
        r.addExperience(50);
        List<String> msgs = r.drainMessages();

        // Rogue shows ONE combined line: HP base, ATK combined (4*2 + 3*2 = 14), DEF base
        boolean hasBase = msgs.stream().anyMatch(msg ->
            msg.equals("Arya Stark reached level 2: +20 Health, +14 Attack, +2 Defense"));
        assertTrue(hasBase, "Base level-up line. Got: " + msgs);
    }

    @Test
    public void testHunterLevelUpMessages() {
        Hunter h = new Hunter("Ygritte", 220, 30, 2, 6);
        h.addExperience(50);
        List<String> msgs = h.drainMessages();

        // Hunter shows ONE combined line.
        // HP: 10*2=20, ATK: (4+2)*2=12, DEF: 2(base) + 2(playerLevel) = 4
        boolean hasBase = msgs.stream().anyMatch(msg ->
            msg.equals("Ygritte reached level 2: +20 Health, +12 Attack, +4 Defense"));
        assertTrue(hasBase, "Base level-up line. Got: " + msgs);
    }

    @Test
    public void testLevelUpMessageCountIsTwo() {
        // Mage level-up produces exactly 2 messages (base + mana/spell line)
        Mage m = new Mage("Melisandre", 100, 5, 1, 300, 30, 15, 5, 6);
        m.addExperience(50);
        List<String> msgs = m.drainMessages();
        assertEquals(2, msgs.size(),
            "Mage one level-up should produce exactly 2 messages. Got: " + msgs);
    }

    // ===================================================================
    // Ability cast messages
    // ===================================================================

    @Test
    public void testWarriorCastMessage() {
        Warrior w = new Warrior("Jon Snow", 300, 30, 4, 3);
        w.setPosition(new Position(5, 5));
        List<Enemy> enemies = new ArrayList<>();
        enemies.add(new Monster('s', "Gold Cloak", 80, 8, 3, 25, 3, new Position(5, 6)));
        w.castAbility(enemies);
        List<String> msgs = w.drainMessages();

        boolean hasCast = msgs.stream().anyMatch(m -> m.contains("Avenger's Shield"));
        assertTrue(hasCast, "Warrior cast should mention Avenger's Shield");
    }

    @Test
    public void testMageCastMessage() {
        Mage m = new Mage("Melisandre", 100, 5, 1, 300, 30, 15, 5, 6);
        m.setPosition(new Position(5, 5));
        List<Enemy> enemies = new ArrayList<>();
        enemies.add(new Monster('s', "Gold Cloak", 80, 8, 3, 25, 3, new Position(5, 6)));
        m.castAbility(enemies);
        List<String> msgs = m.drainMessages();

        assertEquals("Melisandre cast Blizzard.", msgs.get(0),
            "First Blizzard message must be exact");
    }

    @Test
    public void testRogueCastMessage() {
        Rogue r = new Rogue("Arya Stark", 150, 40, 2, 20);
        r.setPosition(new Position(5, 5));
        List<Enemy> enemies = new ArrayList<>();
        enemies.add(new Monster('s', "Gold Cloak", 80, 8, 3, 25, 3, new Position(5, 6)));
        r.castAbility(enemies);
        List<String> msgs = r.drainMessages();

        assertEquals("Arya Stark cast Fan of Knives.", msgs.get(0),
            "First Fan of Knives message must be exact");
    }

    @Test
    public void testMageBlizzardHitMessageFormat() {
        Mage m = new Mage("Melisandre", 100, 5, 1, 300, 30, 15, 5, 6);
        m.setPosition(new Position(5, 5));
        List<Enemy> enemies = new ArrayList<>();
        enemies.add(new Monster('s', "Gold Cloak", 9999, 8, 3, 25, 3, new Position(5, 6)));
        m.castAbility(enemies);
        List<String> msgs = m.drainMessages();

        // Each hit produces two lines: "X rolled N defense points." and "Melisandre hit X for N ability damage."
        boolean hasDefRoll = msgs.stream().anyMatch(msg ->
            msg.matches("Gold Cloak rolled \\d+ defense points\\."));
        boolean hasHit = msgs.stream().anyMatch(msg ->
            msg.matches("Melisandre hit Gold Cloak for \\d+ ability damage\\."));
        assertTrue(hasDefRoll, "Should have 'Gold Cloak rolled N defense points.'");
        assertTrue(hasHit,     "Should have 'Melisandre hit Gold Cloak for N ability damage.'");
    }

    @Test
    public void testRogueFanHitMessageFormat() {
        Rogue r = new Rogue("Arya Stark", 150, 40, 2, 20);
        r.setPosition(new Position(5, 5));
        List<Enemy> enemies = new ArrayList<>();
        enemies.add(new Monster('s', "Gold Cloak", 9999, 8, 3, 25, 3, new Position(5, 6)));
        r.castAbility(enemies);
        List<String> msgs = r.drainMessages();

        boolean hasDefRoll = msgs.stream().anyMatch(msg ->
            msg.matches("Gold Cloak rolled \\d+ defense points\\."));
        boolean hasHit = msgs.stream().anyMatch(msg ->
            msg.matches("Arya Stark hit Gold Cloak for \\d+ ability damage\\."));
        assertTrue(hasDefRoll, "Should have defense roll message");
        assertTrue(hasHit,     "Should have hit message");
    }

    @Test
    public void testMageNotEnoughManaMessage() {
        Mage m = new Mage("Melisandre", 100, 5, 1, 300, 30, 15, 5, 6);
        m.setPosition(new Position(5, 5));
        List<Enemy> enemies = new ArrayList<>();
        enemies.add(new Monster('s', "Gold Cloak", 9999, 8, 3, 25, 3, new Position(5, 6)));

        // Use up mana: 75 → 45 → 15. Third cast fails (15 < 30).
        m.castAbility(enemies); m.drainMessages();
        m.castAbility(enemies); m.drainMessages();
        m.castAbility(enemies); // fails
        List<String> msgs = m.drainMessages();

        assertEquals(1, msgs.size(), "Only one message when blocked by mana");
        assertEquals("Melisandre tried to cast Blizzard, but there was not enough mana: 15/30.",
            msgs.get(0), "Exact not-enough-mana message");
    }

    @Test
    public void testHunterNoArrowsMessage() {
        // Exhaust all 10 arrows
        Hunter h = new Hunter("Ygritte", 220, 30, 2, 6);
        h.setPosition(new Position(5, 5));
        List<Enemy> enemies = new ArrayList<>();
        enemies.add(new Monster('s', "Tank", 9999, 0, 0, 25, 3, new Position(5, 6)));

        for (int i = 0; i < 10; i++) {
            h.castAbility(enemies);
            h.drainMessages();
        }
        assertEquals(0, h.getArrowCount());

        h.castAbility(enemies);
        List<String> msgs = h.drainMessages();
        assertEquals(1, msgs.size(), "Only one message when no arrows");
        assertEquals("Ygritte tried to shoot an arrow but has no arrows.", msgs.get(0));
    }

    // ===================================================================
    // toString — tile characters
    // ===================================================================

    @Test
    public void testPlayerToStringAlive() {
        Warrior w = new Warrior("Jon Snow", 300, 30, 4, 3);
        assertEquals("@", w.toString());
    }

    @Test
    public void testPlayerToStringDead() {
        Warrior w = new Warrior("Jon Snow", 300, 30, 4, 3);
        w.takeDamage(9999);
        assertEquals("X", w.toString(), "Dead player should show as 'X'");
    }
}
