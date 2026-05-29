package dnd.business.visitors;

import dnd.business.units.Enemy;
import dnd.business.units.Player;

public interface OccupantVisitor {
    public void visit(Player player);
    public void visit(Enemy enemy);

}
