package dnd.business.units;

public class Trap extends Enemy {
    private int invisibilityTime;
    private int visibilityTime;
    private int ticksCount;
    private boolean visible;

    public Trap(String name, int healthPool, int healthAmount, int attackPoint, int defencePoint, int invisibilityTime,  int visibilityTime, int experience) {
        super(name, healthPool, healthAmount, attackPoint, defencePoint,experience);
        this.ticksCount = 0;
        this.invisibilityTime = invisibilityTime;
        this.visibilityTime = visibilityTime;
        this.visible = true;
    }
    public void OnGameTick(){
        if(ticksCount < this.visibilityTime){
            this.visible = true;
        }
        else this.visible = false;
        ticksCount++;
        if(this.ticksCount > this.invisibilityTime+this.visibilityTime){this.ticksCount = 0;}
    }
    public void onEnemyTurn(){

    }
}
