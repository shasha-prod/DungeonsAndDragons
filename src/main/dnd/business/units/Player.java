package dnd.business.units;

import dnd.business.board.Floor;
import dnd.business.board.Wall;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class Player extends Unit implements HeroicUnit {
    protected int experience;
    protected int playerLevel;

    protected Random rand = new Random();

    public Player(String name, int healthPool, int healthAmount, int attackPoint, int defencePoint) {
        super(name,healthPool,healthAmount,attackPoint,defencePoint);
        this.experience = 0;
        this.playerLevel = 1;
    }

    public Player(String name, int healthPool, int healthAmount, int attackPoint, int defencePoint, int experience, int playerLevel) {
        super(name,healthPool,healthAmount,attackPoint,defencePoint);
    }

    public boolean levelUp() {
        if(experience > 50*playerLevel) {
            experience = 0;
            playerLevel =  playerLevel + 1;
            healthPool = healthPool + (10*playerLevel);
            healthAmount = healthPool;
            attackPoint = attackPoint + (4*playerLevel);
            defencePoint = defencePoint + (playerLevel);
            return true;
        }
        return false;
    }
    public void accept(Unit unit){
        unit.visit(this);
    }
    @Override
    public void visit(Player player) {
        // Player visiting themselves doesnt do anything
    }

    @Override
    public void visit(Enemy enemy) {
        //
    }

    public void castAbility(java.util.List<Enemy> enemies) {
        //
    }

    public int getPlayerLevel() {
        return this.playerLevel; // Assuming your level variable is named playerLevel
    }

    public void onGameTick() {
        // Leave empty here. Subclasses like Warrior will override it.
    }
    @Override
    public String ToString() {
        return "@";
    }
    protected List<Enemy> getEnemiesInRange(List<Enemy> enemies, double range) {
        List<Enemy> inRange = new ArrayList<>();
        for (Enemy e : enemies) {
            if (e.isAlive() && Range.range(this.position, e.getPosition())< range) {
                inRange.add(e);
            }
        }
        return inRange;
    }

    protected Enemy chooseRandomEnemy(List<Enemy> inRange) {
        if (inRange.isEmpty()) {
            return null;
        }
        return inRange.get(rand.nextInt(inRange.size()));
    }
}
