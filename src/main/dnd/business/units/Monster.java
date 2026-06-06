package dnd.business.units;

import dnd.business.board.GameBoard;

public class Monster extends Enemy{
    private int visionRange;

    public Monster(String name, int healthPool, int healthAmount, int attackPoint, int defencePoint, int visionRange, int experience) {
        super(name, healthPool, healthAmount, attackPoint, defencePoint,experience);
        this.visionRange = visionRange;
    }
    public void onEnemyTurn(){
        if(Range.range(this.play.getPosition(), this.position) < visionRange){
            int dx = this.position.getX() -this.play.getPosition().getX();
            int dy = this.position.getY() -this.play.getPosition().getY();
            if(Math.abs(dx) > Math.abs(dy)){
                if(dx > 0){
                    gm.moveUnit(left);
                }
                else {
                    gm.moveUnit(right);
                }
            }
            else{
                if(dy > 0){
                    gm.moveUnit(up);
                }
                else{
                    gm.moveUnit(down);

                }
            }
        }
    }
}
