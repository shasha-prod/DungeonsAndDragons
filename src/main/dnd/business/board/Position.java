package dnd.business.board;

public class Position {
    private int x;
    private int y;

    //recieves x,y variables and places the object into the specific place.
    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() { return x; }
    public int getY() { return y; }

//    public void setPosition(int x, int y) {
//        this.x = x;
//        this.y = y;
//    }

    //
    public double getCoordinates(Position other) {
        return Math.sqrt(Math.pow(this.x - other.x, 2) + Math.pow(this.y - other.y, 2));
    }

    public int compareTo(Position other) {
        if (this.y != other.y) {
            return Integer.compare(this.y, other.y);
        }
        return Integer.compare(this.x, other.x);
    }
}