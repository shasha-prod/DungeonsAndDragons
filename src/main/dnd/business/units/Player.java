package dnd.business.units;

import dnd.business.board.Floor;
import dnd.business.board.Wall;

public abstract class Player extends Unit implements HeroicUnit {
    protected int experience;
    protected int playerLevel;

    public Player(String name, int healthPool, int healthAmount, int attackPoint, int defencePoint) {
        super(name,healthPool,healthAmount,attackPoint,defencePoint);
        this.experience = 0;
        this.playerLevel = 1;
    }

    public Player(String name, int healthPool, int healthAmount, int attackPoint, int defencePoint, int experience, int playerLevel) {
        super(name,healthPool,healthAmount,attackPoint,defencePoint);
    }

    public boolean levelUp() {
        if(experience > 50*playerLevel) {
            experience = 0;
            playerLevel =  playerLevel + 1;
            healthPool = healthPool + (10*playerLevel);
            healthAmount = healthPool;
            attackPoint = attackPoint + (4*playerLevel);
            defencePoint = defencePoint + (playerLevel);
            return true;
        }
        return false;
    }
    public void accept(Unit unit){
        unit.visit(this);
    }
    @Override
    public void visit(Player player) {
        // Player visiting themselves doesnt do anything
    }

    @Override
    public void visit(Enemy enemy) {
        attack(enemy);
    }
    @Override
    public void castAbility() {

    }
}
