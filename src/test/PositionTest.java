
import dnd.business.board.Position;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PositionTest {

    // ===== Range calculation =====

    @Test
    public void testRangeSamePosition() {
        Position a = new Position(3, 3);
        Position b = new Position(3, 3);
        assertEquals(0.0, a.getCoordinates(b), 0.001);
    }

    @Test
    public void testRangeHorizontal() {
        Position a = new Position(0, 0);
        Position b = new Position(3, 0);
        assertEquals(3.0, a.getCoordinates(b), 0.001);
    }

    @Test
    public void testRangeVertical() {
        Position a = new Position(0, 0);
        Position b = new Position(0, 5);
        assertEquals(5.0, a.getCoordinates(b), 0.001);
    }

    @Test
    public void testRangeDiagonal() {
        Position a = new Position(0, 0);
        Position b = new Position(3, 4);
        assertEquals(5.0, a.getCoordinates(b), 0.001); // 3-4-5 triangle
    }

    @Test
    public void testRangeIsSymmetric() {
        Position a = new Position(1, 2);
        Position b = new Position(4, 6);
        assertEquals(a.getCoordinates(b), b.getCoordinates(a), 0.001);
    }

    @Test
    public void testRangeReturnsDoubleNotTruncatedInt() {
        Position a = new Position(0, 0);
        Position b = new Position(1, 1);
        // sqrt(2) = 1.414..., NOT 1
        assertTrue(a.getCoordinates(b) > 1.4);
        assertTrue(a.getCoordinates(b) < 1.5);
    }

    @Test
    public void testRangeAdjacentCells() {
        Position a = new Position(5, 5);
        // Up, down, left, right — all distance 1.0
        assertEquals(1.0, a.getCoordinates(new Position(5, 4)), 0.001);
        assertEquals(1.0, a.getCoordinates(new Position(5, 6)), 0.001);
        assertEquals(1.0, a.getCoordinates(new Position(4, 5)), 0.001);
        assertEquals(1.0, a.getCoordinates(new Position(6, 5)), 0.001);
    }

    // ===== Boundary: range < 2 (Trap/Rogue range) =====

    @Test
    public void testRangeLessThan2_Adjacent() {
        Position a = new Position(5, 5);
        Position b = new Position(5, 6); // distance = 1
        assertTrue(a.getCoordinates(b) < 2);
    }

    @Test
    public void testRangeLessThan2_Diagonal() {
        Position a = new Position(5, 5);
        Position b = new Position(6, 6); // distance = sqrt(2) = 1.414
        assertTrue(a.getCoordinates(b) < 2);
    }

    @Test
    public void testRangeLessThan2_TwoCellsAway() {
        Position a = new Position(5, 5);
        Position b = new Position(5, 7); // distance = 2, NOT < 2
        assertFalse(a.getCoordinates(b) < 2);
    }

    // ===== Boundary: range < 3 (Warrior range) =====

    @Test
    public void testRangeLessThan3_ExactlyAt3() {
        Position a = new Position(0, 0);
        Position b = new Position(3, 0); // distance = 3, NOT < 3
        assertFalse(a.getCoordinates(b) < 3);
    }

    @Test
    public void testRangeLessThan3_JustUnder() {
        Position a = new Position(0, 0);
        Position b = new Position(2, 2); // distance = sqrt(8) = 2.83
        assertTrue(a.getCoordinates(b) < 3);
    }

    // ===== getX/getY =====

    @Test
    public void testGetters() {
        Position p = new Position(7, 13);
        assertEquals(7, p.getX());
        assertEquals(13, p.getY());
    }
}
