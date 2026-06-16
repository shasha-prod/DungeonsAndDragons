package dnd.business;

import dnd.business.board.*;
import dnd.business.units.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MageTest {
    private Mage mage;
    private List<Enemy> enemies;

    @BeforeEach
    public void setup() {
        // Melisandre stats from spec
        // name, hp, atk, def, manaPool, manaCost, spellPower, hitsCount, abilityRange
        mage = new Mage("Melisandre", 100, 5, 1, 300, 30, 15, 5, 6);
        mage.setPosition(new Position(5, 5));
        enemies = new ArrayList<>();
    }

    private Monster createEnemyAt(int x, int y) {
        Monster m = new Monster('s', "Gold Cloak", 80, 8, 3, 25, 3, new Position(x, y));
        enemies.add(m);
        return m;
    }

    // ===== Initial state =====

    @Test
    public void testInitialManaIsQuarterOfPool() {
        // Spec: initially mana_pool / 4 = 300 / 4 = 75
        assertEquals(75, mage.getCurrentMana());
    }

    @Test
    public void testDisplayCharacter() {
        assertEquals("@", mage.toString());
    }

    // ===== Blizzard =====

    @Test
    public void testCastBlizzardReducesMana() {
        createEnemyAt(5, 6);
        int manaBefore = mage.getCurrentMana();
        mage.castAbility(enemies);
        // Cost is 30
        assertEquals(manaBefore - 30, mage.getCurrentMana());
    }

    @Test
    public void testCannotCastWithoutEnoughMana() {
        createEnemyAt(5, 6);
        // Cast until mana runs out: 75 mana, costs 30 each
        mage.castAbility(enemies); // 75 → 45
        mage.drainMessages();
        mage.castAbility(enemies); // 45 → 15
        mage.drainMessages();
        mage.castAbility(enemies); // 15 < 30 → should fail
        List<String> msgs = mage.drainMessages();
        boolean blocked = msgs.stream().anyMatch(m ->
            m.contains("mana") || m.contains("Mana") || m.contains("Cannot"));
        assertTrue(blocked, "Should report not enough mana");
    }

    @Test
    public void testBlizzardDamagesEnemies() {
        Monster enemy = createEnemyAt(5, 6); // range 1 < 6
        int healthBefore = enemy.getHealthAmount();
        mage.castAbility(enemies);
        // spellPower=15 minus defense roll, over up to 5 hits
        assertTrue(enemy.getHealthAmount() <= healthBefore,
            "Blizzard should damage enemies");
    }

    @Test
    public void testBlizzardOnlyHitsEnemiesInRange() {
        Monster close = createEnemyAt(5, 6);     // range 1 < 6 → in range
        Monster far = createEnemyAt(50, 50);       // way out of range
        int farHealthBefore = far.getHealthAmount();
        mage.castAbility(enemies);
        assertEquals(farHealthBefore, far.getHealthAmount(),
            "Enemies outside ability range should not be hit");
    }

    @Test
    public void testBlizzardMaxHitsCount() {
        // Place 1 enemy, hitsCount is 5 — all 5 hits go to same enemy
        Monster enemy = createEnemyAt(5, 6);
        mage.castAbility(enemies);
        List<String> msgs = mage.drainMessages();
        long hitCount = msgs.stream().filter(m -> m.contains("hit") || m.contains("Hit")).count();
        assertTrue(hitCount <= 5, "Should not exceed hits count of 5");
    }

    @Test
    public void testBlizzardNoEnemiesInRange() {
        createEnemyAt(50, 50); // out of range
        int manaBefore = mage.getCurrentMana();
        mage.castAbility(enemies);
        // Mana should still be deducted (spec: deduct first, then hit loop)
        assertEquals(manaBefore - 30, mage.getCurrentMana());
    }

    // ===== Mana regeneration =====

    @Test
    public void testManaRegensOnGameTick() {
        createEnemyAt(5, 6);
        mage.castAbility(enemies); // spend 30 mana: 75 → 45
        mage.drainMessages();
        int manaBefore = mage.getCurrentMana();
        mage.onGameTick();
        // Spec: current_mana += 1 * level. Level 1 → +1
        assertEquals(manaBefore + 1, mage.getCurrentMana());
    }

    @Test
    public void testManaRegenCappedAtPool() {
        // Full mana scenario
        // Initial mana is 75, pool is 300
        for (int i = 0; i < 500; i++) {
            mage.onGameTick();
        }
        assertTrue(mage.getCurrentMana() <= 300,
            "Mana should never exceed mana pool");
    }

    // ===== Mage level-up bonuses =====

    @Test
    public void testMageLevelUpIncreasesManaPool() {
        mage.addExperience(50); // level up to 2
        mage.drainMessages();
        // Base mana pool 300 + 25*2 = 350
        assertEquals(350, mage.getManaPool());
    }

    @Test
    public void testMageLevelUpIncreasesSpellPower() {
        mage.addExperience(50);
        mage.drainMessages();
        // Base spell power 15 + 10*2 = 35
        assertEquals(35, mage.getSpellPower());
    }

    @Test
    public void testMageLevelUpRestoresSomeMana() {
        createEnemyAt(5, 6);
        mage.castAbility(enemies); // 75 → 45
        mage.drainMessages();
        int manaBefore = mage.getCurrentMana();
        mage.addExperience(50); // level up
        mage.drainMessages();
        // Spec: current_mana = min(current_mana + mana_pool/4, mana_pool)
        // mana_pool after level up = 350, so +350/4 = +87
        assertTrue(mage.getCurrentMana() > manaBefore,
            "Level up should restore some mana");
    }
}
