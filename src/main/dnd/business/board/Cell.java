package dnd.business.board;

import dnd.business.visitors.CellVisitor;

public abstract class Cell {
    public abstract void accept(CellVisitor visitor);

    @Override
    public abstract String toString();
}
