package dnd.business.units;

import dnd.business.visitors.CellVisitor;

public abstract class Unit implements Occupant, CellVisitor {
    private String name;
    private int healthPool;
    private int healthAmount;
    private int attackPoint;
    private int defencePoint;

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
    public String description() {
        return null;
    }


}
