package dnd.business.units;

import dnd.business.board.GameBoard;
import dnd.business.board.Position;

/**
 * A powerful enemy that both chases the player and casts a special ability
 * every {@code abilityFrequency} combat ticks.
 *
 * Behaviour per turn:
 *   1. Always moves toward the player if within visionRange.
 *   2. Attacks if adjacent.
 *   3. Every abilityFrequency-th turn it also performs a special ability
 *      (an enhanced attack that deals double damage).
 */
public class Boss extends Enemy {

    private int visionRange;
    private int abilityFrequency;
    private int combatTicks;

    public Boss(char tile, String name, int healthPool,
                int attackPoint, int defencePoint,
                int visionRange, int abilityFrequency,
                int experienceValue,Position pos) {
        super(tile, name, healthPool, attackPoint, defencePoint, experienceValue, pos);
        this.visionRange      = visionRange;
        this.abilityFrequency = abilityFrequency;
        this.combatTicks      = 0;
    }

    // -----------------------------------------------------------------------
    // Enemy AI turn
    // -----------------------------------------------------------------------

    @Override
    public void onEnemyTurn(Player player, GameBoard board) {
        if (position == null || player.getPosition() == null) return;

        combatTicks++;
        double dist = this.position.getCoordinates(player.getPosition());

        // Special ability fires every abilityFrequency ticks
        if (combatTicks % abilityFrequency == 0) {
            castSpecialAbility(player);
        }

        if (dist <= 1) {
            visit(player);  // routes through visit(Player) for proper combat header
        } else if (dist <= visionRange) {
            // Chase the player
            Position next = stepToward(player.getPosition());
            board.moveUnit(this, next);
        }
        // Beyond vision range — boss stays put
    }

    // -----------------------------------------------------------------------
    // Special ability — double-damage strike
    // -----------------------------------------------------------------------

    private void castSpecialAbility(Player player) {
        int rawDamage = attackPoint * 2;
        player.takeDamage(rawDamage);
        addMessage(name + " shoots " + player.getName() + " for " + rawDamage + " damage");
        if (player.isDead()) {
            addMessage(player.getName() + " was killed by " + name + ".");
        }
    }

    // -----------------------------------------------------------------------
    // Movement helper
    // -----------------------------------------------------------------------

    private Position stepToward(Position target) {
        int dx = target.getX() - position.getX();
        int dy = target.getY() - position.getY();
        if (Math.abs(dx) >= Math.abs(dy)) {
            return new Position(position.getX() + Integer.signum(dx), position.getY());
        } else {
            return new Position(position.getX(), position.getY() + Integer.signum(dy));
        }
    }

    // -----------------------------------------------------------------------
    // Occupant / board display
    // -----------------------------------------------------------------------

    @Override
    public String description() {
        return name
                + "\t\tHealth: "            + healthAmount + "/" + healthPool
                + "\t\tAttack: "            + attackPoint
                + "\t\tDefense: "           + defencePoint
                + "\t\tExperience Value: "  + experienceValue
                + "\t\tVision Range: "      + visionRange
                + "\t\tAbility in: "        + (abilityFrequency - (combatTicks % abilityFrequency))
                + " ticks";
    }
}
