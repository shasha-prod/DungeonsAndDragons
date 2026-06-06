package dnd.business.units;

import dnd.business.board.Floor;
import dnd.business.board.Wall;
import dnd.business.visitors.OccupantVisitor;

public class Enemy extends Unit, OccupantVisitor {
    protected int experienceValue;
    protected Player play;

    public Enemy(String name, int healthPool, int healthAmount, int attackPoint, int defencePoint, int experience) {
        super(name, healthPool, healthAmount, attackPoint, defencePoint);
        this.experienceValue = experience;
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

    @Override
    public void visit(Player player) {

    }

    @Override
    public void visit(Enemy enemy) {

    }
}
