package dnd.business.units;

import dnd.business.board.Position;

import java.util.List;

public class Rogue extends Player {
    private int cost;
    private int currentEnergy;

    public Rogue(String name, int healthPool, int attackPoint, int defencePoint, int cost, Position pos) {
        super(name, healthPool, attackPoint, defencePoint, pos);
        this.cost = cost;
        this.currentEnergy = 100;
    }

    public Rogue( String name, int healthPool, int attackPoint, int defencePoint, int cost) {
        super(name, healthPool, attackPoint, defencePoint, null);
        this.cost = cost;
        this.currentEnergy = 100;
    }
    @Override
    public boolean levelUp() {
        boolean level = super.levelUp();
        if (level) {
            currentEnergy = 100;
            attackPoint  += 3 * playerLevel;
            healthAmount  = healthPool;
            addMessage(name + " reached level " + playerLevel + ": +"
                    + (10 * playerLevel) + " Health, +"
                    + (7  * playerLevel) + " Attack, +"
                    + playerLevel        + " Defense");
        }
        return level;
    }

    public void onGameTick(){
        this.currentEnergy = Math.min(this.currentEnergy+10 , 100);
    }

    @Override
    public void castAbility(List<Enemy> enemies) {
        if(currentEnergy < cost){
            addMessage(name + " tried to cast Fan of Knives, but there was not enough energy: "
                    + currentEnergy + "/" + cost + ".");
            return;
        }
        currentEnergy -= cost;
        addMessage(name + " cast Fan of Knives.");
        List<Enemy> closeEnemies = getEnemiesInRange(enemies, 2);
        for (Enemy enemy : closeEnemies) {
            int defRoll = rand.nextInt(enemy.defencePoint + 1);
            int damage = Math.max(0, attackPoint - defRoll);
            enemy.takeDamage(damage);
            addMessage(enemy.getName() + " rolled " + defRoll + " defense points.");
            addMessage(name + " hit " + enemy.getName() + " for " + damage
                    + " ability damage.");
            if (!enemy.isAlive()) {
                onEnemyKilled(enemy);
            }
        }
    }
    public String description() {
        return name + "\t\tHealth: "   + healthAmount + "/" + healthPool
                + "\t\tAttack: "       + attackPoint
                + "\t\tDefense: "      + defencePoint
                + "\t\tLevel: "        + playerLevel
                + "\t\tExperience: "   + experience + "/" + (50 * playerLevel)
                + "\t\tEnergy: "       + currentEnergy + "/100";
    }

    //For Testing
    public int getCurrentEnergy() {return currentEnergy;}

}
