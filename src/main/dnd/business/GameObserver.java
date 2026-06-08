package dnd.business;
import dnd.business.board.GameBoard;
import dnd.business.units.Player;
import dnd.business.units.Unit;


public interface GameObserver {
    void onStart();
    void onBoardUpdate(GameBoard board);
    void onPlayerStats(Player player);
    void onCombat(Unit attacker, Unit defender, int attackRoll, int defenseRoll, int damage);
    void onLevelUp(Player player);
    void onAbilityCast(Player player, String abilityDescription);
    void onDeath(Unit unit);
    void onMessage(String msg);
}