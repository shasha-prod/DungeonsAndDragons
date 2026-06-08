package dnd.business.board;

import dnd.business.units.Occupant;
import dnd.business.units.Unit;

public class GameBoard {
    private final Cell[][] gameBoard;

    public GameBoard(Cell[][] board) {
        this.gameBoard = board;
    }

    public GameBoard(int width, int height) {
        this.gameBoard = new Cell[width][height];
        emptyBoard();
    }

    public Cell getCell(Position p) {
        return gameBoard[p.getY()][p.getX()];}


    public void emptyBoard(){
        for(int i = 0; i < gameBoard.length; i++){
            for(int j = 0; j < gameBoard[0].length; j++){
                gameBoard[i][j] = null;
            }
        }
    }

    public Occupant getOccupant(Position p) {
        Cell cell = getCell(p);
        OccupantExtractor extractor = new OccupantExtractor();
        cell.accept(extractor);
        return extractor.getOccupant();
    }

    public void setOccupant(Position p, Occupant o) {
        Cell cell = getCell(p);
        OccupantSetter setter = new OccupantSetter(o);
        cell.accept(setter);
    }

    public void setCell(Position pos, Cell cell) {
        gameBoard[pos.getY()][pos.getX()] = cell;
    }

    public void moveUnit(Unit unit, Position from, Position to) {
        Floor oldFloor = (Floor) getCell(from);
        oldFloor.setCurrentOccupant(null);    // package-private

        Floor newFloor = (Floor) getCell(to);
        newFloor.setCurrentOccupant(unit);    // package-private

        unit.setPosition(to);          // package-private
    }
}
