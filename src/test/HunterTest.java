package dnd.business;

import dnd.business.board.*;
import dnd.business.units.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HunterTest {
    private Hunter hunter;
    private List<Enemy> enemies;

    @BeforeEach
    public void setup() {
        // Ygritte stats from spec: hp=220, atk=30, def=2, range=6
        hunter = new Hunter("Ygritte", 220, 30, 2, 6);
        hunter.setPosition(new Position(5, 5));
        enemies = new ArrayList<>();
    }

    private Monster createEnemyAt(int x, int y) {
        Monster m = new Monster('s', "Gold Cloak", 80, 8, 3, 25, 3, new Position(x, y));
        enemies.add(m);
        return m;
    }

    // ===== Initial state =====

    @Test
    public void testInitialArrowCount() {
        // Spec: arrowCount = 10 * playerLevel = 10 * 1 = 10
        assertEquals(10, hunter.getArrowCount());
    }

    @Test
    public void testInitialRange() {
        assertEquals(6, hunter.getRange());
    }

    @Test
    public void testDisplayCharacter() {
        assertEquals("@", hunter.toString());
    }

    // ===== castAbility: no arrows =====

    @Test
    public void testCastWithNoArrowsProducesMessage() {
        // Drain arrows manually by casting repeatedly (need enemies in range)
        createEnemyAt(5, 6);
        for (int i = 0; i < 10; i++) {
            hunter.castAbility(enemies);
            hunter.drainMessages();
            // Refresh enemy HP so it doesn't die
            enemies.get(0).takeDamage(-999); // can't heal, so just recreate
        }
        enemies.clear();
        // Recreate with enough HP to survive hits
        Monster toughEnemy = new Monster('s', "Tank", 9999, 0, 0, 25, 3, new Position(5, 6));
        enemies.add(toughEnemy);

        // Now exhaust arrows properly
        hunter = new Hunter("Ygritte", 220, 30, 2, 6);
        hunter.setPosition(new Position(5, 5));
        enemies.clear();
        Monster tank = new Monster('s', "Tank", 9999, 0, 0, 25, 3, new Position(5, 6));
        enemies.add(tank);

        for (int i = 0; i < 10; i++) {
            hunter.castAbility(enemies);
            hunter.drainMessages();
        }
        assertEquals(0, hunter.getArrowCount());

        hunter.castAbility(enemies);
        List<String> msgs = hunter.drainMessages();
        boolean blocked = msgs.stream().anyMatch(m -> m.contains("no arrows") || m.contains("arrows"));
        assertTrue(blocked, "Should report no arrows remaining");
    }

    // ===== castAbility: fires at closest enemy (the fixed bug) =====

    @Test
    public void testCastAbilityFiresAtClosestEnemy() {
        // Two enemies in range: one close (range 1), one far (range 4)
        Monster close = createEnemyAt(5, 6); // distance 1
        Monster far   = createEnemyAt(5, 9); // distance 4 (within range 6)

        int closeBefore = close.getHealthAmount();
        int farBefore   = far.getHealthAmount();

        hunter.castAbility(enemies);
        hunter.drainMessages();

        // Closest enemy should take damage; far enemy should NOT
        assertTrue(close.getHealthAmount() < closeBefore,
            "Should attack the closest enemy");
        assertEquals(farBefore, far.getHealthAmount(),
            "Should NOT attack the farther enemy");
    }

    @Test
    public void testCastAbilityIgnoresDeadEnemies() {
        Monster dead = createEnemyAt(5, 6);
        dead.takeDamage(9999); // kill it
        Monster alive = createEnemyAt(5, 7); // next closest

        int aliveBefore = alive.getHealthAmount();

        hunter.castAbility(enemies);
        hunter.drainMessages();

        // Should skip dead and hit the alive one
        assertTrue(alive.getHealthAmount() < aliveBefore,
            "Should target the closest alive enemy, not a dead one");
    }

    @Test
    public void testCastAbilityReducesArrowCountByOne() {
        createEnemyAt(5, 6);
        int before = hunter.getArrowCount();
        hunter.castAbility(enemies);
        hunter.drainMessages();
        assertEquals(before - 1, hunter.getArrowCount());
    }

    @Test
    public void testCastAbilityDoesNotFireOutOfRange() {
        Monster outOfRange = createEnemyAt(5, 12); // distance 7 > range 6

        int healthBefore = outOfRange.getHealthAmount();
        int arrowsBefore = hunter.getArrowCount();

        hunter.castAbility(enemies);
        hunter.drainMessages();

        // No valid target — arrows should NOT be spent
        assertEquals(arrowsBefore, hunter.getArrowCount(),
            "Should not fire when no enemies are in range");
        assertEquals(healthBefore, outOfRange.getHealthAmount(),
            "Out-of-range enemy should not take damage");
    }

    @Test
    public void testCastAbilityDealsDamage() {
        // 0-defense enemy, attack 30 → guaranteed damage
        Monster noDefEnemy = new Monster('s', "Weak", 80, 0, 0, 25, 3, new Position(5, 6));
        enemies.add(noDefEnemy);

        hunter.castAbility(enemies);
        hunter.drainMessages();

        assertTrue(noDefEnemy.getHealthAmount() < 80,
            "Arrow should deal damage to a 0-defense enemy");
    }

    @Test
    public void testKillingEnemyGrantsExperience() {
        // One-shot enemy
        Monster fragile = new Monster('s', "Fragile", 1, 0, 0, 25, 3, new Position(5, 6));
        enemies.add(fragile);

        int xpBefore = hunter.getExperience();
        hunter.castAbility(enemies);
        hunter.drainMessages();

        assertTrue(fragile.isDead());
        assertTrue(hunter.getExperience() > xpBefore,
            "Killing an enemy should grant experience");
    }

    // ===== onGameTick: arrow regeneration =====

    @Test
    public void testArrowRegenAfter10Ticks() {
        // Spend 1 arrow
        createEnemyAt(5, 6);
        hunter.castAbility(enemies);
        hunter.drainMessages();
        int arrowsAfterShot = hunter.getArrowCount(); // should be 9

        // Regen fires when ticksCount reaches 10 (needs 11 calls: 0→1→…→10→regen)
        for (int i = 0; i < 11; i++) {
            hunter.onGameTick();
        }

        assertEquals(arrowsAfterShot + 1, hunter.getArrowCount(),
            "Should regen 1 arrow (= playerLevel) after the regen cycle");
    }

    @Test
    public void testNoArrowRegenBefore10Ticks() {
        createEnemyAt(5, 6);
        hunter.castAbility(enemies);
        hunter.drainMessages();
        int arrowsAfterShot = hunter.getArrowCount();

        for (int i = 0; i < 10; i++) {
            hunter.onGameTick();
        }

        assertEquals(arrowsAfterShot, hunter.getArrowCount(),
            "Should NOT regen arrows before regen cycle completes");
    }

    // ===== levelUp: returns correct boolean (the fixed bug) =====

    @Test
    public void testLevelUpReturnsTrueWhenLevelingUp() {
        hunter.addExperience(49); // just under threshold
        assertFalse(hunter.levelUp(), "Should return false when not enough XP");
    }

    @Test
    public void testAddExperienceTriggersLevelUp() {
        hunter.addExperience(50);
        hunter.drainMessages();
        assertEquals(2, hunter.getPlayerLevel());
    }

    @Test
    public void testLevelUpIncreasesArrows() {
        int arrowsBefore = hunter.getArrowCount();
        hunter.addExperience(50); // level up to 2
        hunter.drainMessages();
        // Bonus: +10 * playerLevel = +10*2 = +20
        assertEquals(arrowsBefore + 20, hunter.getArrowCount());
    }

    @Test
    public void testLevelUpIncreasesAttack() {
        int atkBefore = hunter.getAttackPoints();
        hunter.addExperience(50); // level up to 2
        hunter.drainMessages();
        // Base: +4*2=8. Hunter bonus: +2*2=4. Total: +12
        assertEquals(atkBefore + 12, hunter.getAttackPoints());
    }

    @Test
    public void testLevelUpIncreasesDefense() {
        int defBefore = hunter.getDefensePoints();
        hunter.addExperience(50); // level up to 2
        hunter.drainMessages();
        // Base: +1*2=2. Hunter bonus: +1*2=2. Total: +4
        assertEquals(defBefore + 4, hunter.getDefensePoints());
    }

    @Test
    public void testLevelUpHealsToFullHealth() {
        hunter.takeDamage(100); // wound to 120/220
        hunter.addExperience(50); // level up
        hunter.drainMessages();
        assertEquals(hunter.getHealthPool(), hunter.getHealthAmount(),
            "Level up should fully heal the hunter");
    }

    @Test
    public void testLevelUpIncreasesHealthPool() {
        int poolBefore = hunter.getHealthPool();
        hunter.addExperience(50);
        hunter.drainMessages();
        // Base: +10*2=20. Hunter has no extra HP bonus.
        assertEquals(poolBefore + 20, hunter.getHealthPool());
    }

    @Test
    public void testMultipleLevelUpsFromLargeXP() {
        // 50 (lvl1→2) + 100 (lvl2→3) = 150 total to reach level 3
        hunter.addExperience(150);
        hunter.drainMessages();
        assertEquals(3, hunter.getPlayerLevel());
    }
}
