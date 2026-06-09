package dnd.business.units;

import dnd.cli.CLIHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Mage extends Player{
    private int manaPool;
    private int currentMana;
    private int manaCost;
    private int spellPower;
    private int hitsCount;
    private int abilityRange;
    private Random random = new Random();

    public Mage(String name, int healthPool, int healthAmount, int attackPoint, int defencePoint, int experience, int playerLevel, int manaPool, int manaCost,
                int spellPower, int hitsCount, int abilityRange) {
        super(name, healthPool, healthAmount, attackPoint, defencePoint, experience, playerLevel);
        this.manaPool = manaPool;
        this.currentMana = manaPool/4;
        this.manaCost = manaCost;
        this.spellPower = spellPower;
        this.hitsCount = hitsCount;
        this.abilityRange = abilityRange;

    }
    @Override
    public boolean levelUp() {
        boolean level = super.levelUp();
        if(level) {
            manaPool = manaPool + (25 * playerLevel);
            currentMana = Math.min(currentMana + (manaPool / 4), manaPool);
            spellPower = spellPower + (10 * playerLevel);
            addMessage(this.name + " has reached level " + this.playerLevel + " +" + (15 * playerLevel) +
                    " Health, +" + (4 * playerLevel) + " Attack, +" + (2 * playerLevel) + " Defence, +" + (25 * playerLevel)
                    + " Mana Pool, +" + (10 * playerLevel) + " Spell Power");
            return true;
        }
        return false;
    }

    public void gameTick(){
        currentMana = Math.min(currentMana + 1 * playerLevel ,manaPool);
    }

    @Override
    public void castAbility(List<Enemy> enemyList){
        if(currentMana < manaCost){
            addMessage("Cannot cast ability, it costs "+manaCost+" to Mana, and we only have  " + currentMana );
        }
        addMessage(this.name + " casts Blizzard");
        currentMana = currentMana - manaCost;
        int hits = 0;
        List closeEnemies = getEnemiesInRange(enemyList,abilityRange);
        while(hits< hitsCount){
            chooseRandomEnemy(closeEnemies).takeDamage(spellPower);
            hits++;

        }
    }
    public void OnGameTick(){
        currentMana = Math.min(manaPool, currentMana+1*playerLevel);
    }
    public String description() {
        return this.name +"     Health: " + this.healthAmount + "/" + this.healthPool + "     Attack: " + this.attackPoint +
                "     Defence: " + this.defencePoint + "     Level: " + this.playerLevel + "     Experience: " + this.experience +
                "/50" + "    Mana: " + this.manaCost + "/" + this.manaPool +"    Spell Power: " + this.spellPower;
    }

}
