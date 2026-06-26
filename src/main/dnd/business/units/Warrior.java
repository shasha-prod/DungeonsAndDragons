package dnd.business.units;
import dnd.business.board.Position;
import dnd.business.visitors.OccupantVisitor;

import java.util.List;

public class Warrior extends Player {
    private int abilityCooldown;
    private int remainingCooldown;

    public Warrior(String name, int healthPool, int attackPoint, int defencePoint, int abilityCooldown, Position pos) {
        super(name, healthPool, attackPoint, defencePoint, pos);
        this.abilityCooldown = abilityCooldown;
        this.remainingCooldown = 0;
    }

    public Warrior(String name, int healthPool, int attackPoint, int defencePoint, int abilityCooldown) {
        super(name, healthPool, attackPoint, defencePoint, null);
        this.abilityCooldown = abilityCooldown;
        this.remainingCooldown = 0;
    }
    public boolean levelUp(){
        boolean level = super.levelUp();
        if(level){
            remainingCooldown = 0;
            healthPool   += 5 * playerLevel;
            attackPoint  += 2 * playerLevel;
            defencePoint += playerLevel;
            healthAmount  = healthPool;
            addMessage(name + " reached level " + playerLevel + ": +"
                    + (15 * playerLevel) + " Health, +"
                    + (6  * playerLevel) + " Attack, +"
                    + (2  * playerLevel) + " Defense");
        }
        return level;
    }

    @Override
    public void castAbility(List<Enemy> enemies) {
        if(remainingCooldown > 0){
            addMessage(this.name + " tried to cast Avenger's Shield, but there is a cooldown: " + remainingCooldown);
            return;
        }
        else{
            this.remainingCooldown = this.abilityCooldown;
            this.healthAmount = Math.min(this.healthAmount + (10*defencePoint),healthPool);
        }
        List<Enemy> closeEnemies = getEnemiesInRange(enemies, 3);
        Enemy chosen = chooseRandomEnemy(closeEnemies);
        if (chosen == null) {
            addMessage(name + " cast Avenger's Shield but no enemies in range.");
            return;
        }
        chosen.takeDamage(this.healthPool/10);
        addMessage(this.name + " used Avenger's Shield, healing for " + (10*defencePoint));
        if (!chosen.isAlive()) {
            onEnemyKilled(chosen);
        }
    }


    public void onGameTick(){
        if(remainingCooldown > 0){
            remainingCooldown--;
        }
    }
    public String description() {
        return name + "\t\tHealth: " + healthAmount + "/" + healthPool
                + "\t\tAttack: "    + attackPoint
                + "\t\tDefense: "   + defencePoint
                + "\t\tLevel: "     + playerLevel
                + "\t\tExperience: " + experience + "/" + (50 * playerLevel)
                + "\t\tCooldown: "  + remainingCooldown + "/" + abilityCooldown;
    }

    //For testing
    public int getRemainingCooldown() {return this.remainingCooldown;}
}
