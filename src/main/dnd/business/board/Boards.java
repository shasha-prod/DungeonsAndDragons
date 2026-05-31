package dnd.business.board;

public class Boards {
    private final GameBoard[] gameBoard;

    public Boards(GameBoard[] boards) {
        this.gameBoard = boards;
    }

    public GameBoard getLevel(int num) {
        return gameBoard[num];
    }
}
