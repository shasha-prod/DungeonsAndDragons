package dnd.business.units;

public class Mage extends Player{
    private int manaPool;
    private int currentMana;
    private int manaCost;
    private int spellPower;
    private int hitsCount;
    private int abilityRange;


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

    public String description() {
        return this.name +"     Health: " + this.healthAmount + "/" + this.healthPool + "     Attack: " + this.attackPoint +
                "     Defence: " + this.defencePoint + "     Level: " + this.playerLevel + "     Experience: " + this.experience +
                "/50" + "    Mana: " + this.manaCost + "/" + this.manaPool +"    Spell Power: " + this.spellPower;
    }

}
