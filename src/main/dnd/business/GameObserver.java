package dnd.business;
import dnd.business.board.GameBoard;
import dnd.business.units.Player;
import dnd.business.units.Unit;


public interface GameObserver {
    void onStart();
    void onMessage(String msg);
}