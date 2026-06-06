package dnd.business.units;

import dnd.business.board.Cell;
import dnd.business.board.GameBoard;
import dnd.business.board.Position;

import java.util.Random;

public class Monster extends Enemy{
    private int visionRange;

    public Monster(String name, int healthPool, int healthAmount, int attackPoint, int defencePoint, int visionRange, int experience) {
        super(name, healthPool, healthAmount, attackPoint, defencePoint,experience);
        this.visionRange = visionRange;
    }
    public void onEnemyTurn(GameBoard gameBoard, Player play) {
        if(Range.range(play.getPosition(), this.position) < visionRange){
            int dx = this.position.getX() -play.getPosition().getX();
            int dy = this.position.getY() -play.getPosition().getY();
            if(Math.abs(dx) > Math.abs(dy)){
                if(dx > 0){
                    movePosition(gameBoard,new Position(this.position.getX()-1,this.position.getY()));
                }
                else {
                    movePosition(gameBoard,new Position(this.position.getX()+1,this.position.getY()));
                }
            }
            else{
                if(dy > 0) {
                    movePosition(gameBoard, new Position(this.position.getX(), this.position.getY() - 1));
                }
                else{
                    movePosition(gameBoard,new Position(this.position.getX(),this.position.getY()+1));
                }
            }
        }
        else{
            Random random = null;
            int roll = random.nextInt(5);  // 0-4
            switch(roll) {
                case 0: movePosition(gameBoard, new Position(
                        position.getX() - 1, position.getY())); break;
                case 1: movePosition(gameBoard, new Position(
                        position.getX() + 1, position.getY())); break;
                case 2: movePosition(gameBoard, new Position(
                        position.getX(), position.getY() - 1)); break;
                case 3: movePosition(gameBoard, new Position(
                        position.getX(), position.getY() + 1)); break;
                case 4: break;  // stay
            }
        }
    }
}
