package dnd.business.units;

import java.util.List;

public class Rogue extends Player {
    private int cost;
    private int currentEnergy;

    public Rogue(String name, int healthPool, int healthAmount, int attackPoint, int defencePoint, int experience, int playerLevel, int cost) {
        super(name, healthPool, healthAmount, attackPoint, defencePoint, experience, playerLevel);
        this.cost = cost;
        this.currentEnergy = 100;
    }

    @Override
    public boolean levelUp() {
        boolean level = super.levelUp();
        if(level) {
            this.currentEnergy = 100;
            this.attackPoint = this.attackPoint + (3 * playerLevel);
            addMessage(this.name + " has reached level " + this.playerLevel + " +" + (10* playerLevel) +
                    " Health, +"+ (2* playerLevel) + " Attack, +" + (playerLevel) + " Defence");
        }
        return false;
    }

    public void onGameTick(){
        this.currentEnergy = Math.min(this.currentEnergy+10 , 100);
    }

    @Override
    public void castAbility(List<Enemy> enemies) {
        if(currentEnergy < cost){
            addMessage("Cannot cast ability, it costs "+cost+" to use special ability, and we only have  " + currentEnergy );
        }
        currentEnergy -= cost;
        List<Enemy> closeEnemies = getEnemiesInRange(enemies,2);
        for (Enemy enemy :closeEnemies){
            enemy.takeDamage(attackPoint);
        }
    }
    public String description() {
        return this.name +"     Health: " + this.healthAmount + "/" + this.healthPool + "     Attack: " + this.attackPoint +
                "     Defence: " + this.defencePoint + "     Level: " + this.playerLevel + "     Experience: " + this.experience
                + "    Energy: " + this.currentEnergy;
    }
}
