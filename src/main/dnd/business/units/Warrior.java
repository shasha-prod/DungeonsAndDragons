package dnd.business.units;

import dnd.cli.CLIHandler;

import java.util.Random;

public class Warrior extends Player {
    private int abilityCooldown;
    private int remainingCooldown;
    private Random random = new Random();

    public Warrior(String name, int healthPool, int healthAmount, int attackPoint, int defencePoint, int experience, int playerLevel, int abilityCooldown) {
        super(name, healthPool, healthAmount, attackPoint, defencePoint, experience, playerLevel);
        this.abilityCooldown = abilityCooldown;
        this.remainingCooldown = 0;
    }

    public boolean levelUp(){
        boolean level = super.levelUp();
        if(level){
            remainingCooldown=0;
            healthPool = healthPool + (5* playerLevel);
            attackPoint = attackPoint + (2* playerLevel);
            defencePoint = defencePoint + playerLevel;
            addMessage(this.name + " has reached level " + this.playerLevel + " +" + (15* playerLevel) +
                    " Health, +"+ (4* playerLevel) + " Attack, +" + (2* playerLevel) + " Defence");
            return true;
        }
        return false;
    }

    public void gameTick(){
        remainingCooldown--;
    }

    @Override
    public void castAbility() {
        if(remainingCooldown > 0){
            addMessage(this.name + " tried to cast Avenger's Shield, but there is a cooldown: " + remainingCooldown);
        }
        else{
            this.remainingCooldown = this.abilityCooldown;
            this.healthAmount = Math.min(this.healthAmount + (10*defencePoint),healthPool);
        }
        // if(Range.range(this.pos, ))
        this.healthAmount =(this.healthAmount* 9)/10;

        addMessage(this.name + " used Avenger's Shield, healing for " + (10*defencePoint));
    }

    public String description() {
        return this.name +"     Health: " + this.healthAmount + "/" + this.healthPool + "     Attack: " + this.attackPoint +
                "     Defence: " + this.defencePoint + "     Level: " + this.playerLevel +
                "     Experience: " + this.experience + "     Cooldown: " + this.remainingCooldown;
    }

}
