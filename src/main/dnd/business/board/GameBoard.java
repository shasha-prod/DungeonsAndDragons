package dnd.business.board;

import dnd.business.units.Occupant;

public class GameBoard {
    private final Cell[][] gameBoard;

    public GameBoard(Cell[][] board) {
        this.gameBoard = board;
    }

    public Cell getCell(Position p) {
        return gameBoard[p.getY()][p.getX()];
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
}
