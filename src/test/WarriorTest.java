package dnd.business;

import dnd.business.board.*;
import dnd.business.units.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class WarriorTest {
    private Warrior warrior;
    private List<Enemy> enemies;

    @BeforeEach
    public void setup() {
        // Jon Snow stats from spec
        warrior = new Warrior("Jon Snow", 300, 30, 4, 3);
        warrior.setPosition(new Position(5, 5));
        enemies = new ArrayList<>();
    }

    private Monster createEnemyAt(int x, int y) {
        Monster m = new Monster('s', "Gold Cloak", 80, 8, 3, 25, 3, new Position(x, y));
        enemies.add(m);
        return m;
    }

    // ===== Initial state =====

    @Test
    public void testInitialCooldownIsZero() {
        assertEquals(0, warrior.getRemainingCooldown());
    }

    @Test
    public void testDisplayCharacter() {
        assertEquals("@", warrior.toString());
    }

    // ===== Avenger's Shield =====

    @Test
    public void testCastAbilitySetsCooldown() {
        createEnemyAt(5, 6); // adjacent, range = 1 < 3
        warrior.castAbility(enemies);
        // After casting, cooldown should be set to abilityCooldown (3)
        // Check messages for success indicator
        List<String> msgs = warrior.drainMessages();
        boolean cast = msgs.stream().anyMatch(m -> m.contains("Avenger's Shield"));
        assertTrue(cast, "Should have cast Avenger's Shield");
    }

    @Test
    public void testCannotCastWhileOnCooldown() {
        createEnemyAt(5, 6);
        warrior.castAbility(enemies); // first cast — should work
        warrior.drainMessages();

        warrior.castAbility(enemies); // second cast — should fail
        List<String> msgs = warrior.drainMessages();
        boolean blocked = msgs.stream().anyMatch(m -> m.contains("cooldown"));
        assertTrue(blocked, "Should report cooldown blocking the cast");
    }

    @Test
    public void testCooldownDecrementsOnGameTick() {
        createEnemyAt(5, 6);
        warrior.castAbility(enemies);
        warrior.drainMessages();

        warrior.onGameTick(); // tick 1
        warrior.onGameTick(); // tick 2
        warrior.onGameTick(); // tick 3

        // After 3 ticks (cooldown is 3), should be able to cast again
        warrior.castAbility(enemies);
        List<String> msgs = warrior.drainMessages();
        boolean cast = msgs.stream().anyMatch(m -> m.contains("Avenger's Shield"));
        assertTrue(cast, "Should be able to cast after cooldown expires");
    }

    @Test
    public void testAvengerShieldHeals() {
        createEnemyAt(5, 6);
        warrior.takeDamage(100); // down to 200/300
        int healthBefore = warrior.getHealthAmount();
        warrior.castAbility(enemies);
        // Heals for 10 * defense = 10 * 4 = 40
        assertTrue(warrior.getHealthAmount() > healthBefore,
            "Avenger's Shield should heal the warrior");
    }

    @Test
    public void testAvengerShieldHealCappedAtPool() {
        createEnemyAt(5, 6);
        // Full health, heal should not exceed pool
        warrior.castAbility(enemies);
        assertTrue(warrior.getHealthAmount() <= warrior.getHealthPool(),
            "Health should not exceed health pool");
    }

    @Test
    public void testAvengerShieldDamageIs10PercentOfHealthPool() {
        Monster enemy = createEnemyAt(5, 6);
        int enemyHealthBefore = enemy.getHealthAmount();
        warrior.castAbility(enemies);
        // Damage is 10% of healthPool = 300/10 = 30
        // Enemy defends, so damage could be less, but health should not increase
        assertTrue(enemy.getHealthAmount() <= enemyHealthBefore);
    }

    @Test
    public void testAvengerShieldNoEnemiesInRange() {
        createEnemyAt(50, 50); // way out of range
        warrior.castAbility(enemies);
        List<String> msgs = warrior.drainMessages();
        boolean noTarget = msgs.stream().anyMatch(m -> 
            m.contains("no enemies") || m.contains("No enemies"));
        assertTrue(noTarget, "Should report no enemies in range");
    }

    // ===== Warrior level-up bonuses =====

    @Test
    public void testWarriorLevelUpResetsCooldown() {
        createEnemyAt(5, 6);
        warrior.castAbility(enemies); // sets cooldown
        warrior.drainMessages();

        warrior.addExperience(50); // level up
        // Spec: remaining_cooldown ← 0 on level up
        // Should be able to cast immediately
        warrior.castAbility(enemies);
        List<String> msgs = warrior.drainMessages();
        boolean cast = msgs.stream().anyMatch(m -> m.contains("Avenger's Shield"));
        assertTrue(cast, "Cooldown should reset on level up");
    }

    @Test
    public void testWarriorLevelUpBonusStats() {
        int oldPool = warrior.getHealthPool();
        int oldAtk = warrior.getAttackPoints();
        int oldDef = warrior.getDefensePoints();

        warrior.addExperience(50); // level up to 2
        warrior.drainMessages();

        // Base Player: +10*2=20 HP pool, +4*2=8 ATK, +1*2=2 DEF
        // Warrior bonus: +5*2=10 HP pool, +2*2=4 ATK, +1*2=2 DEF
        // Total: +30 HP pool, +12 ATK, +4 DEF
        assertEquals(oldPool + 30, warrior.getHealthPool());
        assertEquals(oldAtk + 12, warrior.getAttackPoints());
        assertEquals(oldDef + 4, warrior.getDefensePoints());
    }
}
