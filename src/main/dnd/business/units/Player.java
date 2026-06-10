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
        super(name,healthPool,attackPoint,defencePoint,pos);
        this.experience = 0;
        this.playerLevel = 0;
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
    public void accept(Unit unit){
        unit.visit(this);
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
        String result = attack(enemy);
        addMessage(result);
        if (!enemy.isAlive()) {
            onEnemyKilled(enemy);
            board.moveUnit(this, this.position, enemy.getPosition());
        }
    }

    protected void onEnemyKilled(Enemy enemy) {
        addMessage(enemy.getName() + " died. " + name + " gained "
                + enemy.getExperienceValue() + " XP");
        experience += enemy.getExperienceValue();
        levelUp();
    }

    public abstract void castAbility(java.util.List<Enemy> enemies);
    public abstract void onGameTick() ;

    public int getPlayerLevel() {
        return this.playerLevel; // Assuming your level variable is named playerLevel
    }

    @Override
    public String toString() {
        return "@";
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
}
