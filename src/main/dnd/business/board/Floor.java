package dnd.business.board;

import dnd.business.units.Enemy;
import dnd.business.units.Occupant;
import dnd.business.visitors.CellVisitor;

public class Floor extends Cell {

    private Occupant currentOccupant;

    //Floors constructor
    public Floor() {
        currentOccupant = null;
    }

    //returns the value of the currentOccupant.
    public Occupant getCurrentOccupant() {
        return currentOccupant;
    }

    //recieves an occupant and replaces the occupant currently on the floor.
    public void setCurrentOccupant(Occupant occupant) {
        this.currentOccupant = occupant;
    }

    @Override
    public void accept(CellVisitor visitor) {
            visitor.visit(this);
    }
    //Returns the string value of a Floor object (with or without an occupant)
    @Override
    public String toString() {
        if (currentOccupant == null) {
            return ".";
        }
        return currentOccupant.toString();
    }

}
