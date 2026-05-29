package dnd.business.visitors;

import dnd.business.board.Floor;
import dnd.business.board.Wall;

public interface CellVisitor {
    public void visit(Wall wall);
    public void visit(Floor floor);

}
