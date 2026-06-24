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
        boolean level = super.levelUp();
        if(level){
            arrowCount = arrowCount + (10* playerLevel);
            attackPoint = attackPoint + (2* playerLevel);
            defencePoint = defencePoint + playerLevel;
            addMessage(this.name + " has reached level " + this.playerLevel + " +" + (10* playerLevel) +
                    " Arrow Count, +"+ (2* playerLevel) + " Attack, +" + (playerLevel) + " Defence");
        }
        return level;
    }
    public String description() {
        return this.name +"     Health: " + this.healthAmount + "/" + this.healthPool + "     Attack: " + this.attackPoint +
                "     Defence: " + this.defencePoint + "     Level: " + this.playerLevel + "     Experience: " + this.experience + "/" + (50 * playerLevel)
                + "    Arrows: " + this.arrowCount + "    Range: " + this.range ;
    }

    @Override
    public void castAbility(List<Enemy> enemies) {
        if(arrowCount <= 0){
            addMessage(this.name + " has no arrows.");
            return;
        }
        List<Enemy> closeEnemies = getEnemiesInRange(enemies, this.range);
        Enemy chosen = closestEnemy(closeEnemies);
        if(chosen != null){
            chosen.takeDamage(attackPoint);
            if (!chosen.isAlive()) {
                onEnemyKilled(chosen);
            }
            arrowCount--;
        }
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
