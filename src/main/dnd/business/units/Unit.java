package dnd.business.units;

import dnd.business.GameObserver;
import dnd.business.board.*;
import dnd.business.visitors.CellVisitor;
import dnd.business.visitors.OccupantVisitor;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class Unit implements Occupant, CellVisitor, OccupantVisitor {
    protected String name;
    protected int healthPool;
    protected int healthAmount;
    protected int attackPoint;
    protected int defencePoint;
    protected Position position;
    protected Position targetPosition = null;
    protected GameBoard board;
    protected List<GameObserver> observers = new ArrayList<>();
    private List<String> messages = new ArrayList<>();
    private static final Random RANDOM = new Random();

    public Unit(String name, int healthPool, int attackPoint, int defencePoint, Position position) {
        this.name = name;
        this.healthPool = healthPool;
        this.healthAmount = healthPool;
        this.attackPoint = attackPoint;
        this.defencePoint = defencePoint;
        this.position = position;
    }

    public Unit(String name, int healthPool, int attackPoint, int defencePoint) {
        this.name = name;
        this.healthPool = healthPool;
        this.healthAmount = healthPool;
        this.attackPoint = attackPoint;
        this.defencePoint = defencePoint;
        this.position = null;    }

    // -----------------------------------------------------------------------
    // Abstract contract
    // -----------------------------------------------------------------------

    public abstract String description();

    // -----------------------------------------------------------------------
    // CellVisitor — Level 1 of double dispatch
    // -----------------------------------------------------------------------

    @Override
    public void visit(Wall wall) {
        // Hit a wall — movement is silently blocked; nothing changes.
    }

    /**
     * Reached a Floor tile.
     * • Empty floor  → move onto it.
     * • Occupied floor → trigger OccupantVisitor dispatch (combat / interaction).
     */
    @Override
    public void visit(Floor floor) {
        if (floor.getCurrentOccupant() == null) {
            // Clear the old cell and move
            board.setOccupant(position, null);
            floor.setCurrentOccupant(this);
            position = targetPosition;
        } else {
            // Level 2 dispatch: let the occupant decide what happens
            floor.getCurrentOccupant().accept(this);
        }
    }

    // -----------------------------------------------------------------------
    // OccupantVisitor — Level 2 of double dispatch (subclasses specialise)
    // -----------------------------------------------------------------------

    @Override
    public abstract void visit(Player player);

    @Override
    public abstract void visit(Enemy enemy);

    // -----------------------------------------------------------------------
    // Occupant interface
    // -----------------------------------------------------------------------

    @Override
    public abstract void accept(OccupantVisitor visitor);

//    /**
//     * Attempt to move to newPosition on the given board.
//     * The board reference is stored so that visit(Floor) can update cells
//     * after a successful move.
//     */
//    public void movePosition(GameBoard gameBoard, Position newPosition) {
//        this.board = gameBoard;          // <-- fixes the null-board NPE
//        this.targetPosition = newPosition;
//        Cell targetCell = gameBoard.getCell(targetPosition);
//        targetCell.accept(this);
//    }

    // -----------------------------------------------------------------------
    // Combat
    // -----------------------------------------------------------------------

    /**
     * Roll-based attack: damage = max(0, attackRoll - defenseRoll).
     * Notifies all observers and announces death if the target dies.
     */
    protected String attack(Unit target) {
        int attackRoll  = RANDOM.nextInt(attackPoint) + 1;
        int defenseRoll = RANDOM.nextInt(Math.max(target.defencePoint, 1)) + 1;
        int damage      = Math.max(0, attackRoll - defenseRoll);
        target.takeDamage(damage);
        for (GameObserver o : observers) {
            o.onCombat(this, target, attackRoll, defenseRoll, damage);
        }
        if (target.isDead()) {
            for (GameObserver o : observers) {
                o.onDeath(target);
            }
        }
        return String.format(
                "%s attacked %s. Roll: %d - %d = %d damage.",
                name, target.name, attackRoll, defenseRoll, damage
        );
    }
    public void takeDamage(int amount) {
        healthAmount = Math.max(0, healthAmount - amount);
    }
    // -----------------------------------------------------------------------
    // Observers / messaging
    // -----------------------------------------------------------------------

    public void addObserver(GameObserver observer) {
        observers.add(observer);
    }

    protected void addMessage(String msg) {
        messages.add(msg);
    }

    public List<String> drainMessages() {
        List<String> copy = new ArrayList<>(messages);
        messages.clear();
        return copy;
    }

    // -----------------------------------------------------------------------
    // Accessors
    // -----------------------------------------------------------------------

    public String getName()       { return name; }
    public Position getPosition() { return position; }
    public void setPosition(Position p) { this.position = p; }
    public boolean isDead()       { return healthAmount <= 0; }
    public boolean isAlive()       { return healthAmount > 0; }

}
