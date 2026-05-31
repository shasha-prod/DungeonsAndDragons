package dnd.business.board;

import dnd.business.units.Occupant;
import dnd.business.visitors.CellVisitor;

class OccupantExtractor implements CellVisitor {
    private Occupant occupant;

    @Override
    public void visit(Wall wall) {
        occupant = null;
    }

    @Override
    public void visit(Floor floor) {
        occupant = floor.getCurrentOccupant();
    }

    public Occupant getOccupant() {
        return occupant;
    }
}
