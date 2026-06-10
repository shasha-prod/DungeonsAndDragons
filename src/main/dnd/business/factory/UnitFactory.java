package dnd.business.factory;

import dnd.business.board.Position;
import dnd.business.units.*;
import dnd.cli.CLIHandler;

import java.util.Dictionary;
import java.util.Hashtable;

public class UnitFactory {

    public UnitFactory() {
    }
    public Player createPlayer(int i) {
        switch (i) {
            case 0: return new Warrior("Jon Snow", 300, 30, 4, 3);
            case 1: return new Warrior("The Hound", 400, 20, 6, 5);
            case 2: return new Mage("Melisandre", 100, 5, 1, 300, 30, 15, 5, 6);
            case 3: return new Mage("Thoros of Myr", 250, 25, 4, 150, 20, 20, 3, 4);
            case 4: return new Rogue("Arya Stark", 150, 40, 2, 20);
            case 5: return new Rogue("Bronn", 250, 35, 3, 50);
            case 6: return new Hunter("Ygritte", 220, 30, 2, 6);
            default: throw new IllegalArgumentException("Invalid player choice");
        }
    }

    public Enemy createEnemy(char tile, Position pos) {
        switch (tile) {
            case 's':
                return new Monster("Gold Cloak", 80, 80, 8, 3, 3, 25, pos);
            case 'k':
                return new Monster("Knight", 200, 200, 14, 8, 4, 50, pos);
            case 'B':
                return new Trap("Bonus Trap", 1, 1, 1, 1, 250, 1, 5, pos);
            case 'q':
                return new Monster("Queen's Guard", 400, 400, 20, 15, 5, 100, pos);
            case 'z':
                return new Monster("Wright", 600, 600, 30, 15, 3, 100, pos);
            case 'b':
                return new Monster("Bear", 1000, 1000, 75, 30, 4, 250, pos);
            case 'g':
                return new Monster("Giant", 1500, 1500, 100, 40, 5, 500, pos);
            case 'w':
                return new Monster("White Walker", 2000, 2000, 150, 50, 6, 1000, pos);
            case 'M':
                return new Monster("The Mountain", 1000, 1000, 60, 25, 6, 500, pos);
            case 'C':
                return new Monster("Queen Cersei", 100, 100, 10, 10, 1, 1000, pos);
            case 'K':
                return new Monster("Night's King", 5000, 5000, 300, 150, 8, 5000, pos);
            case 'Q':
                return new Trap("Queen's Trap", 250, 250, 50, 10, 7, 3, 100, pos);
            case 'D':
                return new Trap("Death Trap", 500, 500, 100, 20, 10, 1, 250, pos);
            default:
                throw new IllegalStateException("Unknown tile: " + tile);
        }
    }
}
