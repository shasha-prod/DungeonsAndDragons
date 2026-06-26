package dnd.business.units;

import dnd.business.board.Position;
import dnd.business.visitors.OccupantVisitor;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Mage extends Player {
    private int manaPool;
    private int currentMana;
    private int manaCost;
    private int spellPower;
    private int hitsCount;
    private int abilityRange;
    private Random random = new Random();

    public Mage(String name, int healthPool, int attackPoint, int defencePoint, int manaPool, int manaCost,
                int spellPower, int hitsCount, int abilityRange, Position pos) {
        super(name, healthPool, attackPoint, defencePoint, pos);
        this.manaPool = manaPool;
        this.currentMana = manaPool / 4;
        this.manaCost = manaCost;
        this.spellPower = spellPower;
        this.hitsCount = hitsCount;
        this.abilityRange = abilityRange;
    }

    public Mage(String name, int healthPool, int attackPoint, int defencePoint, int manaPool, int manaCost,
                int spellPower, int hitsCount, int abilityRange) {
        super(name, healthPool, attackPoint, defencePoint, null);
        this.manaPool = manaPool;
        this.currentMana = manaPool / 4;
        this.manaCost = manaCost;
        this.spellPower = spellPower;
        this.hitsCount = hitsCount;
        this.abilityRange = abilityRange;
    }

    @Override
    public boolean levelUp() {
        boolean level = super.levelUp();
        if (level) {
            manaPool = manaPool + (25 * playerLevel);
            currentMana = Math.min(currentMana + (manaPool / 4), manaPool);
            spellPower = spellPower + (10 * playerLevel);
            addMessage("                +" + (25 * playerLevel) + " maximum mana, +"
                    + (10 * playerLevel) + " spell power");
            return true;
        }
        return false;
    }

    @Override
    public void castAbility(List<Enemy> enemyList) {
        if (currentMana < manaCost) {
            addMessage("Cannot cast ability, it costs " + manaCost + " to Mana, and we only have  " + currentMana);
            return;
        }
        addMessage(name + " cast Blizzard.");
        currentMana = currentMana - manaCost;
        int hits = 0;
        while (hits < hitsCount) {
            List<Enemy> inRange = getEnemiesInRange(enemyList, abilityRange);
            if (inRange.isEmpty()) break;
            Enemy target = chooseRandomEnemy(inRange);
            int defRoll = random.nextInt(target.defencePoint + 1);
            int damage = Math.max(0, spellPower - defRoll);
            target.takeDamage(damage);

            addMessage(target.getName() + " rolled " + defRoll + " defense points.");
            addMessage(name + " hit " + target.getName() + " for " + damage
                    + " ability damage.");

            if (!target.isAlive()) {
                onEnemyKilled(target);
            }
            hits++;
        }
    }

    @Override
    public void onGameTick() {
        currentMana = Math.min(manaPool, currentMana + 1 * playerLevel);
    }

    public String description() {
        return name + "\t\tHealth: "    + healthAmount + "/" + healthPool
                + "\t\tAttack: "        + attackPoint
                + "\t\tDefense: "       + defencePoint
                + "\t\tLevel: "         + playerLevel
                + "\t\tExperience: "    + experience + "/" + (50 * playerLevel)
                + "\t\tMana: "          + currentMana + "/" + manaPool
                + "\t\tSpell Power: "   + spellPower;
    }

    //For testing
    public int getCurrentMana() {
        return currentMana;
    }

    public int getSpellPower() {
        return spellPower;
    }

    public int getManaPool() {
        return manaPool;
    }
}
