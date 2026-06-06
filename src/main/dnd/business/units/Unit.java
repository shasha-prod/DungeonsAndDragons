package dnd.business.units;

import dnd.business.board.Position;
import dnd.business.visitors.CellVisitor;
import dnd.business.visitors.OccupantVisitor;

public abstract class Unit implements Occupant, CellVisitor {
    protected String name;
    protected int healthPool;
    protected int healthAmount;
    protected int attackPoint;
    protected int defencePoint;
    protected Position position;

    public Unit(String name, int healthPool, int healthAmount, int attackPoint, int defencePoint) {
        this.name = name;
        this.healthPool = healthPool;
        this.healthAmount = healthAmount;
        this.attackPoint = attackPoint;
        this.defencePoint = defencePoint;
        this.position = null;
    }
    public Unit(String name, int healthPool, int healthAmount, int attackPoint, int defencePoint, Position position) {
        this.name = name;
        this.healthPool = healthPool;
        this.healthAmount = healthAmount;
        this.attackPoint = attackPoint;
        this.defencePoint = defencePoint;
        this.position = position;
    }


    public Position getPosition() {
        return position;
    }

    public void setPosition(Position p) {
        this.position = p;
    }
    public void accept(OccupantVisitor occupantVisitor) {

    }
    public void visit(){

    }
    public String getName() {
        return name;
    }

}
