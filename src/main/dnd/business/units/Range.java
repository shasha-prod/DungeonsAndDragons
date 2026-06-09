package dnd.business.units;

import dnd.business.board.Position;

public class Range {
    public static int range(Position player, Position enemy){
        return (int) Math.sqrt((player.getX() - enemy.getX())*(player.getX() - enemy.getX()) +
                (player.getY()-enemy.getY())*(player.getY()-enemy.getY()));
    }

}
