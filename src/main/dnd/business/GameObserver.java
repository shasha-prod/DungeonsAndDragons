package dnd.business;

import dnd.business.board.GameBoard;
import dnd.business.units.Player;
import dnd.business.units.Unit;

public interface GameObserver {
    // Combat events
    void onCombat(Unit attacker, Unit defender, int attackRoll, int defenseRoll, int damage);

    // Level Up
    void onLevelUp(Player player);

    // Abilities
    void onAbilityCast(Player player, String abilityDescription);

    // Death
    void onDeath(Unit unit);

    // Game State
    void onBoardUpdate(GameBoard board);
    void onMessage(String msg);
    void onPlayerStats(Player player);
}
