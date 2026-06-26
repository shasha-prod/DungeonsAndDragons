package dnd.business.units;

import dnd.business.board.GameBoard;
import dnd.business.board.Position;
import dnd.business.visitors.OccupantVisitor;

public abstract class Enemy extends Unit {

    protected int experienceValue;
    protected char tile;

    public Enemy(char tile,String name, int healthPool,
                 int attackPoint, int defencePoint, int experienceValue, Position pos) {
        super(name, healthPool, attackPoint, defencePoint,pos);
        this.experienceValue = experienceValue;
        this.tile = tile;
    }

    // -----------------------------------------------------------------------
    // OccupantVisitor — enemy attacks a player; enemy-on-enemy is a no-op
    // -----------------------------------------------------------------------

    @Override
    public void visit(Player player) {
        addMessage(name + " engaged in combat with " + player.getName() + ".");
        addMessage(this.description());
        addMessage(player.description());
        attack(player);   // attack() now adds the 3 roll/damage lines itself
        if (player.isDead()) {
            addMessage(player.getName() + " was killed by " + name + ".");
        }
    }

    @Override
    public void visit(Enemy enemy) {
        // Two enemies occupying the same tile — ignored.
    }

    // -----------------------------------------------------------------------
    // Occupant — Level 2 double-dispatch back into the OccupantVisitor
    // -----------------------------------------------------------------------

    @Override
    public void accept(OccupantVisitor visitor) {
        visitor.visit(this);
    }

    // -----------------------------------------------------------------------
    // Enemy AI — subclasses define their own behaviour
    // -----------------------------------------------------------------------

    /**
     * Called once per game tick for this enemy's turn.
     *
     * @param player the current player (used for targeting / combat)
     * @param board  the live game board (used for movement)
     */
    public abstract void onEnemyTurn(Player player, GameBoard board);

    public int getExperienceValue() { return experienceValue; }

    /** Returns the cardinal neighbour one step closer to the target. */
    protected Position stepToward(Position target) {
        int dx = target.getX() - position.getX();
        int dy = target.getY() - position.getY();
        if (Math.abs(dx) >= Math.abs(dy)) {
            return new Position(position.getX() + Integer.signum(dx), position.getY());
        } else {
            return new Position(position.getX(), position.getY() + Integer.signum(dy));
        }
    }

    public String toString() { return "" + tile; }

}
