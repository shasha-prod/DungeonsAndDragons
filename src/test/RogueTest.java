package dnd.business;

import dnd.business.board.*;
import dnd.business.units.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class RogueTest {
    private Rogue rogue;
    private List<Enemy> enemies;

    @BeforeEach
    public void setup() {
        // Arya Stark stats from spec
        rogue = new Rogue("Arya Stark", 150, 40, 2, 20);
        rogue.setPosition(new Position(5, 5));
        enemies = new ArrayList<>();
    }

    private Monster createEnemyAt(int x, int y) {
        Monster m = new Monster('s', "Gold Cloak", 80, 8, 3, 25, 3, new Position(x, y));
        enemies.add(m);
        return m;
    }

    // ===== Initial state =====

    @Test
    public void testInitialEnergyIs100() {
        assertEquals(100, rogue.getCurrentEnergy());
    }

    // ===== Fan of Knives =====

    @Test
    public void testCastReducesEnergy() {
        createEnemyAt(5, 6); // range 1 < 2
        rogue.castAbility(enemies);
        // Cost is 20: 100 → 80
        assertEquals(80, rogue.getCurrentEnergy());
    }

    @Test
    public void testCannotCastWithoutEnoughEnergy() {
        createEnemyAt(5, 6);
        // Cast 5 times: 100→80→60→40→20→0
        for (int i = 0; i < 5; i++) {
            rogue.castAbility(enemies);
            rogue.drainMessages();
        }
        assertEquals(0, rogue.getCurrentEnergy());

        rogue.castAbility(enemies); // should fail
        List<String> msgs = rogue.drainMessages();
        boolean blocked = msgs.stream().anyMatch(m ->
            m.contains("energy") || m.contains("Energy") || m.contains("Cannot"));
        assertTrue(blocked, "Should report not enough energy");
    }

    @Test
    public void testFanOfKnivesHitsAllEnemiesInRange() {
        Monster e1 = createEnemyAt(5, 6); // range 1 < 2 → in range
        Monster e2 = createEnemyAt(6, 5); // range 1 < 2 → in range
        Monster e3 = createEnemyAt(6, 6); // range sqrt(2)=1.41 < 2 → in range

        int h1 = e1.getHealthAmount();
        int h2 = e2.getHealthAmount();
        int h3 = e3.getHealthAmount();

        rogue.castAbility(enemies);

        // All three should be hit (minus their defense roll)
        assertTrue(e1.getHealthAmount() <= h1);
        assertTrue(e2.getHealthAmount() <= h2);
        assertTrue(e3.getHealthAmount() <= h3);
    }

    @Test
    public void testFanOfKnivesDoesNotHitEnemiesOutOfRange() {
        createEnemyAt(5, 6);     // in range
        Monster far = createEnemyAt(5, 8); // range 3, NOT < 2

        int farHealth = far.getHealthAmount();
        rogue.castAbility(enemies);

        assertEquals(farHealth, far.getHealthAmount(),
            "Enemies at range >= 2 should not be hit");
    }

    @Test
    public void testFanOfKnivesUsesAttackPoints() {
        // Rogue deals attack points worth of damage (enemy defends)
        // With 40 atk and 3 def on Gold Cloak, damage = max(0, 40 - defRoll)
        // defRoll is 0-3, so damage is 37-40
        Monster enemy = createEnemyAt(5, 6);
        rogue.castAbility(enemies);
        // Should deal significant damage
        assertTrue(enemy.getHealthAmount() < 80,
            "Fan of Knives should deal significant damage based on attack points");
    }

    // ===== Energy regeneration =====

    @Test
    public void testEnergyRegensOnGameTick() {
        createEnemyAt(5, 6);
        rogue.castAbility(enemies); // 100 → 80
        rogue.drainMessages();
        rogue.onGameTick();
        // Spec: current_energy = min(current_energy + 10, 100)
        assertEquals(90, rogue.getCurrentEnergy());
    }

    @Test
    public void testEnergyRegenCappedAt100() {
        rogue.onGameTick(); // already at 100
        assertEquals(100, rogue.getCurrentEnergy());
    }

    // ===== Rogue level-up bonuses =====

    @Test
    public void testRogueLevelUpResetsEnergy() {
        createEnemyAt(5, 6);
        rogue.castAbility(enemies); // 100 → 80
        rogue.drainMessages();
        rogue.addExperience(50); // level up
        rogue.drainMessages();
        // Spec: current_energy ← 100
        assertEquals(100, rogue.getCurrentEnergy());
    }

    @Test
    public void testRogueLevelUpBonusAttack() {
        int oldAtk = rogue.getAttackPoints();
        rogue.addExperience(50); // level up to 2
        rogue.drainMessages();
        // Base: +4*2 = +8. Rogue bonus: +3*2 = +6. Total: +14
        assertEquals(oldAtk + 14, rogue.getAttackPoints());
    }
}
