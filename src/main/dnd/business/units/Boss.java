package dnd.business.units;

import dnd.business.board.GameBoard;
import dnd.business.board.Position;

import java.util.Random;

/**
 * An enemy that chases and periodically fires a ranged special ability
 * ("Shoebodybop") when the player is within vision range.
 *
 * Turn logic (per assignment spec):
 *   if dist < visionRange:
 *       if combatTicks == abilityFrequency → reset ticks, cast ability
 *       else                               → tick++, move/attack toward player
 *   else:
 *       reset ticks, random movement
 */
public class Boss extends Enemy {

    private final int visionRange;
    private final int abilityFrequency;
    private int combatTicks;
    private final Random rand = new Random();

    public Boss(char tile, String name, int healthPool,
                int attackPoint, int defencePoint,
                int visionRange, int abilityFrequency,
                int experienceValue, Position pos) {
        super(tile, name, healthPool, attackPoint, defencePoint, experienceValue, pos);
        this.visionRange      = visionRange;
        this.abilityFrequency = abilityFrequency;
        this.combatTicks      = 0;
    }

    // -----------------------------------------------------------------------
    // Enemy AI turn (follows assignment spec exactly)
    // -----------------------------------------------------------------------

    @Override
    public void onEnemyTurn(Player player, GameBoard board) {
        if (position == null || player.getPosition() == null) return;

        int dist = Range.range(this.position, player.getPosition());

        if (dist < visionRange) {
            if (combatTicks == abilityFrequency) {
                combatTicks = 0;
                castSpecialAbility(player);
            } else {
                combatTicks++;
                if (dist <= 1) {
                    visit(player);
                } else {
                    Position next = stepToward(player.getPosition());
                    board.moveUnit(this, next);
                }
            }
        } else {
            combatTicks = 0;
            Position next = randomStep();
            if (next != null && next.getX() >= 0 && next.getY() >= 0) {
                try { movePosition(board, next); }
                catch (ArrayIndexOutOfBoundsException ignored) { /* hit board edge */ }
            }
        }
    }

    // -----------------------------------------------------------------------
    // Special ability — Shoebodybop (ranged; player can partially defend)
    // -----------------------------------------------------------------------

    private void castSpecialAbility(Player player) {
        addMessage(name + " shoots " + player.getName() + " for " + attackPoint + " damage");
        int defRoll = rand.nextInt(player.defencePoint + 1);
        int damage  = Math.max(0, attackPoint - defRoll);
        player.takeDamage(damage);
        addMessage(player.getName() + " rolled " + defRoll + " defense points.");
        addMessage(name + " hit " + player.getName() + " for " + damage + " ability damage.");
        if (player.isDead()) {
            addMessage(player.getName() + " was killed by " + name + ".");
        }
    }

    // -----------------------------------------------------------------------
    // Movement helpers
    // -----------------------------------------------------------------------

    private Position randomStep() {
        Position[] candidates = {
            new Position(position.getX() - 1, position.getY()),
            new Position(position.getX() + 1, position.getY()),
            new Position(position.getX(),     position.getY() - 1),
            new Position(position.getX(),     position.getY() + 1),
            null
        };
        return candidates[rand.nextInt(5)];
    }

    // -----------------------------------------------------------------------
    // Board display
    // -----------------------------------------------------------------------

    @Override
    public String description() {
        return name
                + "\t\tHealth: "            + healthAmount + "/" + healthPool
                + "\t\tAttack: "            + attackPoint
                + "\t\tDefense: "           + defencePoint
                + "\t\tExperience Value: "  + experienceValue
                + "\t\tVision Range: "      + visionRange
                + "\t\tAbility in: "        + (abilityFrequency - combatTicks)
                + " ticks";
    }

    // For testing
    public int getCombatTicks()      { return combatTicks; }
    public int getAbilityFrequency() { return abilityFrequency; }
}
