package dnd.business.units;
import dnd.business.board.Position;
import dnd.business.visitors.OccupantVisitor;

import java.util.List;
import java.util.Random;

public class Warrior extends Player {
    private int abilityCooldown;
    private int remainingCooldown;
    private Random random = new Random();

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
            remainingCooldown=0;
            healthPool = healthPool + (5* playerLevel);
            attackPoint = attackPoint + (2* playerLevel);
            defencePoint = defencePoint + playerLevel;
            addMessage(this.name + " has reached level " + this.playerLevel + " +" + (15* playerLevel) +
                    " Health, +"+ (4* playerLevel) + " Attack, +" + (2* playerLevel) + " Defence");
            return true;
        }
        return false;
    }

    public void gameTick(){
        remainingCooldown--;
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
    }


    public void onGameTick(){
        if(remainingCooldown > 0){
            remainingCooldown--;
        }
    }
    public String description() {
        return this.name +"     Health: " + this.healthAmount + "/" + this.healthPool + "     Attack: " + this.attackPoint +
                "     Defence: " + this.defencePoint + "     Level: " + this.playerLevel +
                "     Experience: " + this.experience + "     Cooldown: " + this.remainingCooldown;
    }

    @Override
    public void accept(OccupantVisitor visitor) {

    }

}
