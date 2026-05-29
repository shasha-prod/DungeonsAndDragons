package dnd.business.units;

import dnd.business.board.Floor;
import dnd.business.board.Wall;

public class Enemy extends Unit {
    private int experienceValue;

    public Enemy(String name, int healthPool, int healthAmount, int attackPoint, int defencePoint) {
        super(name, healthPool, healthAmount, attackPoint, defencePoint);
        this.experienceValue = experienceValue;
    }

    @Override
    public String ToString() {
        return "";
    }

    @Override
    public void visit(Wall wall) {

    }

    @Override
    public void visit(Floor floor) {

    }
}
