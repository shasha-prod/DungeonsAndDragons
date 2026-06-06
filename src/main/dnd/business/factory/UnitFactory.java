package dnd.business.factory;

import dnd.business.board.Position;
import dnd.business.units.*;
import dnd.cli.CLIHandler;

import java.util.Dictionary;
import java.util.Hashtable;

public class UnitFactory {
    private Player[] players;
    private Dictionary<Character,Enemy> enemyDictionary;
    private CLIHandler clientHandler;

    public UnitFactory() {
        this.players = new Player[7];
        createPlayerArray();
        this.enemyDictionary = new Hashtable<>();
        createEnemyDictionary();
    }
    public void createPlayerArray(){
        players[0] = new Warrior("John Snow", 300,300,30,4,0, 1,3);
        players[1] = new Warrior("The Hound", 400,400,20,6,0, 1,5);
        players[2] = new Mage("Melisandre", 100,100,5,1,0, 1,300,30,15,5,6);
        players[3] = new Mage("Thoros of Myr", 250,250,25,4,0, 1,150,20,20,3,4);
        players[4] = new Rogue("Arya Stark", 150,150,45,2,0, 1,20);
        players[5] = new Rogue("Bronn", 250,250,35,3,0, 1,50);
        players[6] = new Hunter("Ygritte", 220,220,30,2,0, 1,6);
    }
    public void createEnemyDictionary(){
        enemyDictionary.put('s',new Monster("Gold Cloak",80,80,8,3,3,25));
        enemyDictionary.put('k', new Monster("Knight",200,200,14,8,4,50));
        enemyDictionary.put('q', new Monster("Queen's Guard",400,400,20,15,5,100));
        enemyDictionary.put('z', new Monster("Wright",        600,  600,  30, 15, 3, 100));
        enemyDictionary.put('b', new Monster("Bear",         1000, 1000,  75, 30, 4, 250));
        enemyDictionary.put('g', new Monster("Giant",        1500, 1500, 100, 40, 5, 500));
        enemyDictionary.put('w', new Monster("White Walker", 2000, 2000, 150, 50, 6, 1000));
        enemyDictionary.put('M', new Monster("The Mountain", 1000, 1000,  60, 25, 6, 500));
        enemyDictionary.put('C', new Monster("Queen Cersei",  100,  100,  10, 10, 1, 1000));
        enemyDictionary.put('K', new Monster("Night's King", 5000, 5000, 300,150, 8, 5000));
        enemyDictionary.put('B', new Trap("Bonus Trap",    1,   1,   1,  1, 5, 1,  250));
        enemyDictionary.put('Q', new Trap("Queen's Trap",  250, 250, 50, 10, 7, 3,  100));
        enemyDictionary.put('D', new Trap("Death Trap",    500, 500, 100, 20, 10, 1, 250));
    }

    public Player createPlayer(int i) {
        Player p =  players[i];
        return p;
    }

    public Enemy createEnemy(char tile, Position pos) {
        if(enemyDictionary.get(tile) != null) {
            Enemy e =  enemyDictionary.get(tile);
            e.setPosition(pos);
        }
        clientHandler.onMessage("Illegal char entered!");
    }

}
