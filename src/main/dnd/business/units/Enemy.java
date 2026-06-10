package dnd.business.units;

import dnd.business.board.GameBoard;
import dnd.business.board.Position;
import dnd.business.visitors.OccupantVisitor;

public abstract class Enemy extends Unit {

    protected int experienceValue;

    public Enemy(String name, int healthPool,
                 int attackPoint, int defencePoint, int experienceValue, Position pos) {
        super(name, healthPool, attackPoint, defencePoint,pos);
        this.experienceValue = experienceValue;
    }

    // -----------------------------------------------------------------------
    // OccupantVisitor — enemy attacks a player; enemy-on-enemy is a no-op
    // -----------------------------------------------------------------------

    @Override
    public void visit(Player player) {
        attack(player);
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

}
