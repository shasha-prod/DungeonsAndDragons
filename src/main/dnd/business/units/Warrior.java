package dnd.business.units;

import dnd.cli.CLIHandler;

public class Warrior extends Player {
    private int abilityCooldown;
    private int remainingCooldown;
    private CLIHandler clientHandler;

    public Warrior(String name, int healthPool, int healthAmount, int attackPoint, int defencePoint, int experience, int playerLevel, int abilityCooldown) {
        super(name, healthPool, healthAmount, attackPoint, defencePoint, experience, playerLevel);
        this.abilityCooldown = abilityCooldown;
        this.remainingCooldown = 0;
        this.clientHandler = new CLIHandler();
    }

    public void levelUp(){
        remainingCooldown=0;
        super.levelUp();
        healthPool = healthPool + (5* playerLevel);
        attackPoint = attackPoint + (2* playerLevel);
        defencePoint = defencePoint + playerLevel;
        clientHandler.onMessage(this.name + " has reached level " + this.playerLevel + " +" + (15* playerLevel) +
                " Health, +"+ (4* playerLevel) + " Attack, +" + (2* playerLevel) + " Defence");
    }

    public void gameTick(){
        remainingCooldown--;
    }

    @Override
    public void castAbility() {
        if(remainingCooldown > 0){
            clientHandler.onMessage("Cannot cast ability, cooldown at: " + remainingCooldown);
        }
        else{
            this.remainingCooldown = this.abilityCooldown;
            this.healthAmount = Math.min(this.healthAmount + (10*defencePoint),healthPool);
        }
        // if(Range.range(this.pos, ))
        this.healthAmount =(this.healthAmount* 9)/10;
    }

    public String description() {
        return this.name +"     Health: " + this.healthAmount + "/" + this.healthPool + "     Attack: " + this.attackPoint +
                "     Defence: " + this.defencePoint + "     Level: " + this.playerLevel +
                "     Experience: " + this.experience + "     Cooldown: " + this.remainingCooldown;
    }

}
