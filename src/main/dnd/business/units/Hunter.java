package dnd.business.units;

import dnd.business.board.Position;
import dnd.business.visitors.OccupantVisitor;

import java.util.ArrayList;
import java.util.List;

public class Hunter extends Player{
    private int range;
    private int arrowCount;
    private int ticksCount;

    public Hunter(String name, int healthPool, int attackPoint, int defencePoint, int range, Position pos) {
        super(name, healthPool, attackPoint, defencePoint, pos);
        this.range = range;
        this.arrowCount = 10* playerLevel;
        this.ticksCount = 0;
    }
    public Hunter(String name, int healthPool, int attackPoint, int defencePoint, int range) {
        super(name, healthPool, attackPoint, defencePoint, null);
        this.range = range;
        this.arrowCount = 10* playerLevel;
        this.ticksCount = 0;
    }

    public boolean levelUp(){
        int oldDefence = defencePoint;
        boolean level = super.levelUp();
        if(level){
            arrowCount   += 10 * playerLevel;
            attackPoint  += 2  * playerLevel;
            defencePoint += oldDefence;
            healthAmount  = healthPool;
            addMessage(name + " reached level " + playerLevel + ": +"
                    + (10 * playerLevel)         + " Health, +"
                    + (6  * playerLevel)         + " Attack, +"
                    + (playerLevel + oldDefence) + " Defense");
        }
        return level;
    }
    public String description() {
        return name + "\t\tHealth: "  + healthAmount + "/" + healthPool
                + "\t\tAttack: "      + attackPoint
                + "\t\tDefense: "     + defencePoint
                + "\t\tLevel: "       + playerLevel
                + "\t\tExperience: "  + experience + "/" + (50 * playerLevel)
                + "\t\tArrows: "      + arrowCount
                + "\t\tRange: "       + range;
    }

    @Override
    public void castAbility(List<Enemy> enemies) {
        if(arrowCount <= 0){
            addMessage(name + " tried to shoot an arrow but has no arrows.");
            return;
        }
        List<Enemy> closeEnemies = getEnemiesInRange(enemies, this.range);
        Enemy chosen = closestEnemy(closeEnemies);
        if(chosen == null){
            addMessage(name + " tried to shoot an arrow but there were no enemies in range.");
            return;
        }
        addMessage(name + " fired an arrow at " + chosen.getName() + ".");
        int defRoll = rand.nextInt(chosen.defencePoint + 1);
        int damage = Math.max(0, attackPoint - defRoll);
        chosen.takeDamage(damage);
        addMessage(chosen.getName() + " rolled " + defRoll + " defense points.");
        addMessage(name + " hit " + chosen.getName() + " for " + damage + " ability damage.");
        if (!chosen.isAlive()) {
            onEnemyKilled(chosen);
        }
        arrowCount--;
    }

    @Override
    public void onGameTick() {
        if(ticksCount == 10){
            arrowCount = arrowCount + playerLevel;
            ticksCount = 0;
        }
        else{
            ticksCount++;
        }

    }

    // For testing
    public int getArrowCount() { return arrowCount; }
    public int getRange()       { return range; }

    private Enemy closestEnemy(List<Enemy> closeEnemies){
        int minimum = Integer.MAX_VALUE;
        Enemy chosen = null;
        for (Enemy e : closeEnemies) {
            if (e.isAlive() && Range.range(this.position, e.getPosition()) < minimum) {
                minimum = Range.range(this.position, e.getPosition());
                chosen = e;
            }
        }
        return chosen;
    }


}
