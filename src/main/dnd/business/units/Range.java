package dnd.business.units;

import dnd.business.board.Position;

public class Range {
    public static class range(Position a, Position b){
        return Math.sqrt((a.getX() - b.getX())*(a.getX() - b.getX()) + (a.getY()-b.getY())*(a.getY()-b.getY()));
    }
}
