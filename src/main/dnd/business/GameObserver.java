package dnd.business;

public interface GameObserver {
    void onMessage(String message);
    void onAbilityCast(String message);
}
