package dnd.business.units;

import dnd.business.board.GameBoard;
import dnd.business.board.Position;

/**
 * A stationary enemy that alternates between visible and invisible states.
 *
 * Visible   → shown as 'T' on the board; attacks any player that steps on it.
 * Invisible → shown as '.' (indistinguishable from empty floor); still attacks.
 *
 * Visibility cycle (per tick):
 *   ticks 0 .. visibilityTime-1  → visible
 *   ticks visibilityTime .. visibilityTime+invisibilityTime-1 → invisible
 *   then resets.
 */
public class Trap extends Enemy {

    private int invisibilityTime;
    private int visibilityTime;
    private int ticksCount;
    private boolean visible;

    /**
     * @param experienceValue  XP awarded when the trap is destroyed
     * @param visibilityTime   how many ticks the trap is shown per cycle
     * @param invisibilityTime how many ticks the trap is hidden per cycle
     */
    public Trap(char tile, String name, int healthPool,
                int attackPoint, int defencePoint,
                int experienceValue,
                int visibilityTime, int invisibilityTime,
                Position pos) {
        super(tile, name, healthPool, attackPoint, defencePoint, experienceValue, pos);
        this.experienceValue  = experienceValue;
        this.visibilityTime   = visibilityTime;
        this.invisibilityTime = invisibilityTime;
        this.ticksCount       = 0;
        this.visible          = true;
    }

    // -----------------------------------------------------------------------
    // Enemy AI turn — traps are stationary; they only tick their visibility
    // -----------------------------------------------------------------------

    @Override
    public void onEnemyTurn(Player player, GameBoard board) {
        onGameTick();
        // Traps are stationary but attack any player within striking range (< 2 tiles).
        if (position != null && player.getPosition() != null
                && Range.range(position, player.getPosition()) < 2) {
            visit(player);  // routes through visit(Player) for proper combat header + death message
        }
    }

    // -----------------------------------------------------------------------
    // Visibility cycle
    // -----------------------------------------------------------------------

    /** Advance the visibility cycle by one tick. */
    public void onGameTick() {
        ticksCount++;
        if (ticksCount >= visibilityTime + invisibilityTime) {
            ticksCount = 0;
        }
        visible = ticksCount < visibilityTime;
    }

    public boolean isVisible() {
        return visible;
    }

    // -----------------------------------------------------------------------
    // Occupant / board display
    // -----------------------------------------------------------------------

    /**
     * When invisible the trap blends into the floor ('.').
     * When visible it shows as 'T'.
     */
    @Override
    public String toString() {
        if (visible) {
            return String.valueOf(tile);
        }
        return ".";
    }

    @Override
    public String description() {
        return name
                + "\t\tHealth: "            + healthAmount + "/" + healthPool
                + "\t\tAttack: "            + attackPoint
                + "\t\tDefense: "           + defencePoint
                + "\t\tExperience Value: "  + experienceValue
                + "\t\tVisible: "           + visible;
    }
}
