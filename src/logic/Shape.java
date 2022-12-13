package logic;

public class Shape {
    int min, max;

    public Shape (Position position1, Position position2) {
        int dx = Math.abs(position1.getX() - position2.getX());
        int dy = Math.abs(position1.getY() - position2.getY());
        min = Math.min(dx, dy);
        max = Math.max(dx, dy);
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }

    public boolean isShapeOf(int min, int max) {
        return this.min == min && this.max == max;
    }
}
