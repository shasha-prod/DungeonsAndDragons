package dnd.business.units;

import dnd.cli.CLIHandler;

public class Rogue extends Player {
    private int cost;
    private int currentEnergy;
    private CLIHandler clientHandler = new CLIHandler();

    public Rogue(String name, int healthPool, int healthAmount, int attackPoint, int defencePoint, int experience, int playerLevel, int cost) {
        super(name, healthPool, healthAmount, attackPoint, defencePoint, experience, playerLevel);
        this.cost = cost;
        this.currentEnergy = 100;
    }

    @Override
    public boolean levelUp() {
        boolean level = super.levelUp();
        if(level) {
            this.currentEnergy = 100;
            this.attackPoint = this.attackPoint + (3 * playerLevel);
            clientHandler.onMessage(this.name + " has reached level " + this.playerLevel + " +" + (10* playerLevel) +
                    " Health, +"+ (2* playerLevel) + " Attack, +" + (playerLevel) + " Defence");
        }
        return false;
    }

    public void onGameTick(){
        this.currentEnergy = Math.min(this.currentEnergy+10 , 100);
    }

    @Override
    public void castAbility() {
        if(currentEnergy < cost){
            clientHandler.onMessage("Cannot cast ability, it costs "+cost+" to use special ability, and we only have  " + currentEnergy );
        }
        currentEnergy -= cost;
        // for enemy in range <2 reduse health: health -attack points

    }
    public String description() {
        return this.name +"     Health: " + this.healthAmount + "/" + this.healthPool + "     Attack: " + this.attackPoint +
                "     Defence: " + this.defencePoint + "     Level: " + this.playerLevel + "     Experience: " + this.experience
                + "    Energy: " + this.currentEnergy;
    }
}
