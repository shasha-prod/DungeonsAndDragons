package dnd.business.board;

import dnd.business.visitors.CellVisitor;

public class Wall extends Cell {

    public Wall() {
    }

    @Override
    public void accept(CellVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return "#";
    }
}
