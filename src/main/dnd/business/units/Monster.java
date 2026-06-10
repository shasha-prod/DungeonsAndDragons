package dnd.business.units;

import dnd.business.board.GameBoard;
import dnd.business.board.Position;

import java.util.Random;

/**
 * A standard roaming enemy.
 * • Within visionRange of the player → moves one step closer (and attacks when adjacent).
 * • Outside visionRange              → moves randomly or stays put.
 */
public class Monster extends Enemy {

    private int visionRange;
    private static final Random RANDOM = new Random();

    public Monster(String name, int healthPool,
                   int attackPoint, int defencePoint,
                   int visionRange, int experienceValue,Position pos) {
        super(name, healthPool, attackPoint, defencePoint, experienceValue,pos);
        this.visionRange = visionRange;
    }

    // -----------------------------------------------------------------------
    // Enemy AI turn  (signature matches Enemy.onEnemyTurn)
    // -----------------------------------------------------------------------

    @Override
    public void onEnemyTurn(Player player, GameBoard board) {
        if (position == null || player.getPosition() == null) return;

        int dist = Range.range(position, player.getPosition());

        if (dist <= 1) {
            // Already adjacent — attack instead of stepping
            attack(player);
            return;
        }

        Position next = (dist <= visionRange) ? stepToward(player.getPosition()) : randomStep();
        if (next != null) {
            movePosition(board, next);
        }
    }

    // -----------------------------------------------------------------------
    // Movement helpers
    // -----------------------------------------------------------------------

    /** Returns the cardinal neighbour one step closer to the target. */
    private Position stepToward(Position target) {
        int dx = target.getX() - position.getX();
        int dy = target.getY() - position.getY();
        if (Math.abs(dx) >= Math.abs(dy)) {
            return new Position(position.getX() + Integer.signum(dx), position.getY());
        } else {
            return new Position(position.getX(), position.getY() + Integer.signum(dy));
        }
    }

    /** Random cardinal step; case 4 = stay put (returns null). */
    private Position randomStep() {
        switch (RANDOM.nextInt(5)) {
            case 0: return new Position(position.getX() - 1, position.getY());
            case 1: return new Position(position.getX() + 1, position.getY());
            case 2: return new Position(position.getX(),     position.getY() - 1);
            case 3: return new Position(position.getX(),     position.getY() + 1);
            default: return null; // stay
        }
    }

    // -----------------------------------------------------------------------
    // Occupant / board display
    // -----------------------------------------------------------------------

    @Override
    public String toString() { return "m"; }

    @Override
    public String description() {
        return name
                + "     Health: "  + healthAmount + "/" + healthPool
                + "     Attack: "  + attackPoint
                + "     Defence: " + defencePoint;
    }
}
