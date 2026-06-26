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
            healthAmount  = healthPool;   // full-heal after Rogue HP bonus
            addMessage("                +" + (3 * playerLevel) + " bonus attack, energy restored");
        }
        return level;   // was always returning false — broke level-up chain
    }

    public void onGameTick(){
        this.currentEnergy = Math.min(this.currentEnergy+10 , 100);
    }

    @Override
    public void castAbility(List<Enemy> enemies) {
        if(currentEnergy < cost){
            addMessage("Cannot cast ability, it costs "+cost+" to use special ability, and we only have  " + currentEnergy );
            return;
        }
        currentEnergy -= cost;
        addMessage(name + " cast Fan of Knives.");
        List<Enemy> closeEnemies = getEnemiesInRange(enemies, 2);
        if (closeEnemies.isEmpty()) {
            addMessage(name + " cast special ability but no enemies in range.");
        }
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
