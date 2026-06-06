package dnd.business.units;

import dnd.business.board.*;
import dnd.business.visitors.CellVisitor;
import dnd.business.visitors.OccupantVisitor;

import java.util.ArrayList;
import java.util.List;

public abstract class Unit implements Occupant, CellVisitor, OccupantVisitor {
    protected String name;
    protected int healthPool;
    protected int healthAmount;
    protected int attackPoint;
    protected int defencePoint;
    protected Position position;
    protected Position targetPosition = null;
    protected GameBoard board;
    private List<String> messages = new ArrayList<>();



    public Unit(String name, int healthPool, int healthAmount, int attackPoint, int defencePoint, Position position) {
        this.name = name;
        this.healthPool = healthPool;
        this.healthAmount = healthAmount;
        this.attackPoint = attackPoint;
        this.defencePoint = defencePoint;
        this.position = position;
    }

    public Unit(String name, int healthPool, int healthAmount, int attackPoint, int defencePoint) {
        this.name = name;
        this.healthPool = healthPool;
        this.healthAmount = healthAmount;
        this.attackPoint = attackPoint;
        this.defencePoint = defencePoint;
        this.position = null;
    }
    protected void addMessage(String msg) {
        messages.add(msg);
    }

    public List<String> drainMessages() {
        List<String> copy = new ArrayList<>(messages);
        messages.clear();
        return copy;
    }
    @Override
    public abstract void visit(Player player);

    @Override
    public abstract void visit(Enemy enemy);

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position p) {
        this.position = p;
    }
    public void movePosition(GameBoard gameBoard, Position newPosition){
        this.targetPosition = newPosition;
        Cell targetCell = gameBoard.getCell(targetPosition);
        targetCell.accept(this);
    }
    public void visit(Wall wall){
        // player/enemy has hit a wall, the unit doesnt move
    }
    public void visit(Floor floor){
        if(floor.getCurrentOccupant() == null) {
            floor.setCurrentOccupant(this);
            board.moveUnit(this, this.position, this.targetPosition);
        }
        else{
            floor.getCurrentOccupant().accept(this);
        }

    }
    public String getName() {
        return name;
    }

}
