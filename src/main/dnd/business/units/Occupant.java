package dnd.business.units;

import dnd.business.visitors.OccupantVisitor;

public interface Occupant {
    /**
     * Returns the single-character board representation of this occupant.
     * Used by Floor.toString() to render the map tile.
     */
    String ToString();

    /**
     * Double-dispatch entry point (Level 2 of the Visitor pattern).
     * Concrete occupants call visitor.visit(this) so the visitor can react
     * to the exact runtime type without instanceof / casting.
     */
    void accept(OccupantVisitor visitor);
}
