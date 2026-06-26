package dnd.business.units;

import dnd.business.board.Floor;
import dnd.business.board.Position;
import dnd.business.board.Wall;
import dnd.business.visitors.OccupantVisitor;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class Player extends Unit implements HeroicUnit {
    protected int experience;
    protected int playerLevel;

    protected Random rand = new Random();

    public Player(String name, int healthPool, int attackPoint, int defencePoint, Position pos) {
        super(name, healthPool, attackPoint, defencePoint, pos);
        this.experience   = 0;
        this.playerLevel  = 1;   // players begin at level 1
    }

    public boolean levelUp() {
        if(experience >= 50*playerLevel) {
            experience -= 50 * playerLevel;
            playerLevel =  playerLevel + 1;
            healthPool = healthPool + (10*playerLevel);
            healthAmount = healthPool;
            attackPoint = attackPoint + (4*playerLevel);
            defencePoint = defencePoint + (playerLevel);
            return true;
        }
        return false;
    }
    @Override
    public void accept(OccupantVisitor visitor) {
        visitor.visit(this);  // 'this' is Player → calls visit(Player)
    }
    @Override
    public void visit(Player player) {
        // Player visiting themselves doesnt do anything
    }
    @Override
    public void visit(Enemy enemy) {
        addMessage(name + " engaged in combat with " + enemy.getName() + ".");
        addMessage(this.description());
        addMessage(enemy.description());
        attack(enemy);   // attack() now adds the 3 roll/damage lines itself
        if (!enemy.isAlive()) {
            onEnemyKilled(enemy);
            board.moveUnit(this, enemy.getPosition());
        }
    }

    protected void onEnemyKilled(Enemy enemy) {
        addMessage(enemy.getName() + " died. " + name + " gained "
                + enemy.getExperienceValue() + " XP");
        addExperience(enemy.getExperienceValue());   // handles level-up loop + notifications
    }

    public abstract void castAbility(java.util.List<Enemy> enemies);
    public abstract void onGameTick() ;

    public int getPlayerLevel() {
        return playerLevel;
    }

    @Override
    public String toString() {
        return isDead() ? "X" : "@";
    }
    protected List<Enemy> getEnemiesInRange(List<Enemy> enemies, double range) {
        List<Enemy> inRange = new ArrayList<>();
        for (Enemy e : enemies) {
            if (e.isAlive() && Range.range(this.position, e.getPosition())< range) {
                inRange.add(e);
            }
        }
        return inRange;
    }

    protected Enemy chooseRandomEnemy(List<Enemy> inRange) {
        if (inRange.isEmpty()) {
            return null;
        }
        return inRange.get(rand.nextInt(inRange.size()));
    }

    // -----------------------------------------------------------------------
    // Accessors (also used in tests)
    // -----------------------------------------------------------------------

    public int getHealthPool()   { return healthPool; }
    public int getExperience()   { return experience; }

    public void addExperience(int xp) {
        experience += xp;
        while (levelUp()) {
            addMessage(name + " reached level " + playerLevel + ": +"
                    + (10 * playerLevel) + " Health, +"
                    + (4  * playerLevel) + " Attack, +"
                    + playerLevel        + " Defense");
        }
    }

}
