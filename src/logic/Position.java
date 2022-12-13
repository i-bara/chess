package logic;

public class Position {
    private int x, y;

    public Position (int x, int y) {
        if (x >= 1 && x <= 9 && y >= 1 && y <= 10) {
            this.x = x;
            this.y = y;
        }
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int distance(Position position1) {
        return (position1.getX() - x) * (position1.getX() - x) + (position1.getY() - y) * (position1.getY() - y);
    }

    public Position leg(Position position1) {
        return new Position(x + (position1.getX() - x) / 2, y + (position1.getY() - y) / 2);
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Position position) {
            return x == position.getX() && y == position.getY();
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return x + 13333 * y;
    }
}
