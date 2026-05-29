package dnd.business.units;

import dnd.business.board.Floor;
import dnd.business.board.Wall;

public abstract class Player extends Unit implements HeroicUnit {
    protected int experience;
    protected int playerLevel;

    public Player(String name, int healthPool, int healthAmount, int attackPoint, int defencePoint, int experience, int playerLevel) {
        super(name,healthPool,healthAmount,attackPoint,defencePoint);
        this.experience = experience;
        this.playerLevel = playerLevel;
    }

    @Override
    public String ToString() {
        return "";
    }


        @Override
    public void visit(Wall wall) {

    }

    @Override
    public void visit(Floor floor) {

    }

    @Override
    public void castAbility() {

    }
}
