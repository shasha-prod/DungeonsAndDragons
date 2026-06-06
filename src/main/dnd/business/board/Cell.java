package dnd.business.board;

import dnd.business.visitors.CellVisitor;

public abstract class Cell {
    private Position pos;
    public abstract void accept(CellVisitor visitor);

    @Override
    public abstract String toString();
}
