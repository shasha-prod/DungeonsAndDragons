package dnd.business.board;

import dnd.business.units.Occupant;
import dnd.business.units.Unit;

public class GameBoard {
    private final Cell[][] gameBoard;

    //GameBoard constructor when given a fully formatted matrix of the GameBoard.
    public GameBoard(Cell[][] board) {
        this.gameBoard = board;
    }

    //GameBoard constructor when wanting to create a default board, recieves width and height of the Gameboard.
    public GameBoard(int width, int height) {
        this.gameBoard = new Cell[height][width];
        emptyBoard();
    }

    //Returns a specific Cell in the board as per the position of the Cell.
    public Cell getCell(Position p) {
        return gameBoard[p.getY()][p.getX()];}

    //Inserts default values in the board.
    public void emptyBoard(){
        for(int i = 0; i < gameBoard.length; i++){
            for(int j = 0; j < gameBoard[0].length; j++){
                gameBoard[i][j] = null;
            }
        }
    }

    //Recieves a position of a Cell in a GameBoard and a new Occupant to place on the cell and sets the Occupant on the Cell.
    public void setOccupant(Position p, Occupant o) {
        Cell cell = getCell(p);
        OccupantSetter setter = new OccupantSetter(o);
        cell.accept(setter);
    }
    //Recieves a position of a Cell in a GameBoard and a new Cell to add and sets the Cell.
    public void setCell(Position pos, Cell cell) {
        gameBoard[pos.getY()][pos.getX()] = cell;
    }

    // The only method that controls the movement of a Unit on the GameBoard.
    //Recieves the Unit we want to move, and moves it to the new Position of the Unit.
    public void moveUnit(Unit unit, Position to) {
        Floor oldFloor = (Floor) getCell(unit.getPosition());
        oldFloor.setCurrentOccupant(null);
        Floor newFloor = (Floor) getCell(to);
        newFloor.setCurrentOccupant(unit);
        unit.setPosition(to);
    }

    // -----------------------------------------------------------------------
    // Board rendering — used by CLIHandler.onBoardUpdate
    // -----------------------------------------------------------------------

    //prints the GameBoard object in string format.
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int row = 0; row < gameBoard.length; row++) {
            for (int col = 0; col < gameBoard[row].length; col++) {
                Cell cell = gameBoard[row][col];
                sb.append(cell != null ? cell.toString() : " ");
            }
            sb.append(System.lineSeparator());
        }
        return sb.toString();
    }
}
