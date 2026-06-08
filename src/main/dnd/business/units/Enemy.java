package dnd.business.units;

import dnd.business.board.Floor;
import dnd.business.board.Wall;
import dnd.business.visitors.OccupantVisitor;

public class Enemy extends Unit {
    protected int experienceValue;

    public Enemy(String name, int healthPool, int healthAmount, int attackPoint, int defencePoint, int experience) {
        super(name,healthPool,healthAmount,attackPoint,defencePoint);
        this.experienceValue = experience;
    }

    @Override
    public void visit(Player player) {
        attack(player);
    }

    @Override
    public void visit(Enemy enemy) {
        //enemy visiting enemy doesnt do anything
    }
    public void accept(Unit unit){
        unit.visit(this);
    }

    public abstract void onEnemyTurn(Player player, dnd.business.board.GameBoard board);
}
