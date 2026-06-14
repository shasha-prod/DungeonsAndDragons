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

    public Boss(String name, int healthPool,
                int attackPoint, int defencePoint,
                int visionRange, int abilityFrequency,
                int experienceValue,Position pos) {
        super(name, healthPool, attackPoint, defencePoint, experienceValue, pos);
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
            // Adjacent — normal attack (special already fired above if applicable)
            attack(player);
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
        player.healthAmount -= rawDamage;
        addMessage(name + " unleashes a devastating ability, dealing " + rawDamage + " damage!");
        for (var o : observers) {
            o.onAbilityCast(player, name + "'s Special Strike");
        }
        if (player.isDead()) {
            for (var o : observers) { o.onDeath(player); }
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
    public String toString() { return "B"; }

    @Override
    public String description() {
        return name
                + "     Health: "          + healthAmount + "/" + healthPool
                + "     Attack: "          + attackPoint
                + "     Defence: "         + defencePoint
                + "     Ability in: "      + (abilityFrequency - (combatTicks % abilityFrequency))
                + " ticks";
    }
}
