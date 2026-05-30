package dnd.business.units;

public class Monster extends Enemy{
    private int visionRange;

    public Monster(String name, int healthPool, int healthAmount, int attackPoint, int defencePoint, int visionRange) {
        super(name, healthPool, healthAmount, attackPoint, defencePoint);
        this.visionRange = visionRange;
    }
    public void onEnemyTurn(){

    }
}
