package dnd.business.board;

import dnd.business.units.Occupant;
import dnd.business.visitors.CellVisitor;

class OccupantSetter implements CellVisitor {
    private final Occupant occupant;

    OccupantSetter(Occupant occupant) {
        this.occupant = occupant;
    }

    @Override
    public void visit(Wall wall) {
        throw new IllegalStateException("Cannot place an occupant on a Wall.");
    }

    @Override
    public void visit(Floor floor) {
        floor.setCurrentOccupant(occupant);
    }
}
