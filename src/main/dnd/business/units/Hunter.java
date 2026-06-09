package dnd.business.units;

import java.util.List;

public class Hunter extends Player{
    private int range;
    private int arrowCount;
    private int ticksCount;

    public Hunter(String name, int healthPool, int healthAmount, int attackPoint, int defencePoint, int experience, int playerLevel, int range) {
        super(name, healthPool, healthAmount, attackPoint, defencePoint, experience, playerLevel);
        this.range = range;
        this.arrowCount = 10* playerLevel;
        this.ticksCount = 0;
    }


    public String description() {
        return this.name +"     Health: " + this.healthAmount + "/" + this.healthPool + "     Attack: " + this.attackPoint +
                "     Defence: " + this.defencePoint + "     Level: " + this.playerLevel + "     Experience: " + this.experience
                + "    Arrows: " + this.arrowCount + "    Range: " + this.range ;
    }

    @Override
    public void castAbility(List<Enemy> enemies) {
        if(arrowCount < 0){
            addMessage(this.name + " has no arrows.");
        }
        List<Enemy> closeEnemies = getEnemiesInRange(enemies, this.range);
        Enemy chosen = chooseRandomEnemy(closeEnemies);
        chosen.takeDamage(attackPoint);
    }


}
