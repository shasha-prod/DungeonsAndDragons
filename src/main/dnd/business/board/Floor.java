package dnd.business.board;

import dnd.business.units.Occupant;
import dnd.business.visitors.CellVisitor;

public class Floor extends Cell {
    private Occupant currentOccupant;

    public Occupant getCurrentOccupant() {
        return currentOccupant;
    }

    public void setCurrentOccupant(Occupant occupant) {
        this.currentOccupant = occupant;
    }

    @Override
    public void accept(CellVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        if (currentOccupant == null) {
            return ".";
        }
        return currentOccupant.ToString();
    }
}
