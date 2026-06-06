package dnd.business.factory;

import dnd.business.units.Player;

public class UnitFactory {
    private Player[] players;
    public UnitFactory() {
        this.players = new Player[5];
        createPlayerArray();
    }
    public void createPlayerArray(){
        players[0] = new Warrior()
    }
    public static createEnemy(char enemy, Position pos){

    }

    public Player createPlayer(int i) {
    }
}
