package dnd.business.units;

import dnd.business.visitors.CellVisitor;
import dnd.business.visitors.OccupantVisitor;

public abstract class Unit implements Occupant, CellVisitor {
    protected String name;
    protected int healthPool;
    protected int healthAmount;
    protected int attackPoint;
    protected int defencePoint;

    public Unit(String name, int healthPool, int healthAmount, int attackPoint, int defencePoint) {
        this.name = name;
        this.healthPool = healthPool;
        this.healthAmount = healthAmount;
        this.attackPoint = attackPoint;
        this.defencePoint = defencePoint;
    }
    //
    public void accept(OccupantVisitor occupantVisitor) {

    }
    public void visit(){

    }
    public String getName() {
        return name;
    }

}
