
import dnd.business.board.*;
import dnd.business.units.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CombatAndLevelingTest {

    // ===================================================================
    // COMBAT SYSTEM
    // ===================================================================

    // --- takeDamage ---

    @Test
    public void testTakeDamageReducesHealth() {
        Warrior w = new Warrior("Test", 100, 10, 5, 3);
        w.takeDamage(30);
        assertEquals(70, w.getHealthAmount());
    }

    @Test
    public void testTakeDamageCannotGoBelowZero() {
        Warrior w = new Warrior("Test", 50, 10, 5, 3);
        w.takeDamage(999);
        assertEquals(0, w.getHealthAmount());
    }

    @Test
    public void testTakeDamageZeroDamage() {
        Warrior w = new Warrior("Test", 100, 10, 5, 3);
        w.takeDamage(0);
        assertEquals(100, w.getHealthAmount());
    }

    // --- isAlive / isDead ---

    @Test
    public void testIsAliveAtFullHealth() {
        Warrior w = new Warrior("Test", 100, 10, 5, 3);
        assertTrue(w.isAlive());
        assertFalse(w.isDead());
    }

    @Test
    public void testIsDeadAtZeroHealth() {
        Warrior w = new Warrior("Test", 100, 10, 5, 3);
        w.takeDamage(100);
        assertFalse(w.isAlive());
        assertTrue(w.isDead());
    }

    @Test
    public void testIsAliveAtOneHealth() {
        Warrior w = new Warrior("Test", 100, 10, 5, 3);
        w.takeDamage(99);
        assertTrue(w.isAlive());
    }

    // --- attack method ---

    @Test
    public void testAttackProducesNonNullString() {
        Warrior attacker = new Warrior("Attacker", 100, 10, 5, 3);
        Warrior defender = new Warrior("Defender", 100, 10, 5, 3);
        String result = attacker.attack(defender);
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    public void testAttackReducesDefenderHealth() {
        // Run multiple times since combat is random
        Warrior attacker = new Warrior("Attacker", 100, 50, 0, 3); // high atk
        Warrior defender = new Warrior("Defender", 100, 10, 0, 3); // 0 def
        int startHealth = defender.getHealthAmount();

        // With 0 defense, attacker always deals damage (unless roll is 0)
        // Run 20 times, at least some should deal damage
        boolean damageDealt = false;
        for (int i = 0; i < 20; i++) {
            defender = new Warrior("Defender", 100, 10, 0, 3);
            attacker.attack(defender);
            if (defender.getHealthAmount() < 100) {
                damageDealt = true;
                break;
            }
        }
        assertTrue(damageDealt, "Attack should deal damage at least sometimes with 0 defense");
    }

    @Test
    public void testAttackNeverDealsNegativeDamage() {
        // High defense, low attack — damage should be 0, never negative (healing)
        Warrior attacker = new Warrior("Weak", 100, 1, 0, 3);
        Warrior defender = new Warrior("Tank", 100, 10, 50, 3);

        for (int i = 0; i < 50; i++) {
            int before = defender.getHealthAmount();
            attacker.attack(defender);
            assertTrue(defender.getHealthAmount() <= before,
                "Health should never increase from being attacked");
        }
    }

    // ===================================================================
    // PLAYER LEVELING
    // ===================================================================

    @Test
    public void testPlayerStartsAtLevel1() {
        Warrior w = new Warrior("Test", 100, 10, 5, 3);
        assertEquals(1, w.getPlayerLevel());
    }

    @Test
    public void testPlayerStartsWithZeroExperience() {
        Warrior w = new Warrior("Test", 100, 10, 5, 3);
        assertEquals(0, w.getExperience());
    }

    @Test
    public void testLevelUpAt50XP() {
        Warrior w = new Warrior("Test", 100, 10, 5, 3);
        // Level 1 needs 50*1 = 50 XP to level up
        w.addExperience(50);
        assertEquals(2, w.getPlayerLevel());
    }

    @Test
    public void testExperienceRemainsAfterLevelUp() {
        Warrior w = new Warrior("Test", 100, 10, 5, 3);
        // Give 60 XP: 60 >= 50 → level up, remainder = 60 - 50 = 10
        w.addExperience(60);
        assertEquals(2, w.getPlayerLevel());
        assertEquals(10, w.getExperience());
    }

    @Test
    public void testMultipleLevelUpsFromLargeXP() {
        Warrior w = new Warrior("Test", 100, 10, 5, 3);
        // Level 1 needs 50, level 2 needs 100: 50 + 100 = 150
        w.addExperience(150);
        assertEquals(3, w.getPlayerLevel());
    }

    @Test
    public void testNotEnoughXPDoesNotLevel() {
        Warrior w = new Warrior("Test", 100, 10, 5, 3);
        w.addExperience(49);
        assertEquals(1, w.getPlayerLevel());
        assertEquals(49, w.getExperience());
    }

    @Test
    public void testLevelUpIncreasesHealthPool() {
        Warrior w = new Warrior("Test", 100, 10, 5, 3);
        int oldPool = w.getHealthPool();
        w.addExperience(50); // levels up to 2
        // Base: +10*level = +20. Warrior bonus: +5*level = +10. Total: +30
        assertTrue(w.getHealthPool() > oldPool);
    }

    @Test
    public void testLevelUpFullHeals() {
        Warrior w = new Warrior("Test", 100, 10, 5, 3);
        w.takeDamage(50); // down to 50 HP
        w.addExperience(50); // level up — should full heal
        assertEquals(w.getHealthPool(), w.getHealthAmount());
    }

    @Test
    public void testLevelUpIncreasesAttack() {
        Warrior w = new Warrior("Test", 100, 10, 5, 3);
        int oldAtk = w.getAttackPoints();
        w.addExperience(50);
        // Base: +4*level = +8. Warrior bonus: +2*level = +4. Total: +12
        assertTrue(w.getAttackPoints() > oldAtk);
    }

    @Test
    public void testLevelUpIncreasesDefense() {
        Warrior w = new Warrior("Test", 100, 10, 5, 3);
        int oldDef = w.getDefensePoints();
        w.addExperience(50);
        // Base: +1*level = +2. Warrior bonus: +1*level = +2. Total: +4
        assertTrue(w.getDefensePoints() > oldDef);
    }
}
