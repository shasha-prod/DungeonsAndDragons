package dnd.business.units;

public class Warrior extends Player {
    private int abilityCooldown;
    private int remainingCooldown;

    public Warrior(String name, int healthPool, int healthAmount, int attackPoint, int defencePoint, int experience, int playerLevel, int abilityCooldown) {
        super(name, healthPool, healthAmount, attackPoint, defencePoint, experience, playerLevel);
        this.abilityCooldown = abilityCooldown;
        this.remainingCooldown = 0;
    }

    public String description() {
        return this.name +"     Health: " + this.healthAmount + "/" + this.healthPool + "     Attack: " + this.attackPoint +
                "     Defence: " + this.defencePoint + "     Level: " + this.playerLevel +
                "     Experience: " + this.experience + "     Cooldown: " + this.remainingCooldown;
    }
}
