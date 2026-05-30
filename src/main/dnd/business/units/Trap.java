package dnd.business.units;

public class Trap extends Enemy {
    private int invisibilityTime;
    private int visibilityTime;
    private int ticksCount;
    private boolean visible;

    public Trap(String name, int healthPool, int healthAmount, int attackPoint, int defencePoint, int invisibilityTime,  int visibilityTime) {
        super(name, healthPool, healthAmount, attackPoint, defencePoint);
        this.ticksCount = 0;
        this.invisibilityTime = invisibilityTime;
        this.visibilityTime = visibilityTime;
        this.visible = true;
    }

    public void onEnemyTurn(){

    }
}
