package logic.piece;

import logic.Chessboard;
import logic.Player;
import logic.Position;
import logic.Shape;

import java.awt.*;

public abstract class Piece {
    protected Chessboard chessboard;
    protected Player player;
    protected Position position;
    private Image image;

    public Piece(Chessboard chessboard, Player player, Position position, Image image) {
        this.chessboard = chessboard;
        this.player = player;
        this.position = position;
        this.image = image;
        chessboard.add(this);
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public Player getPlayer() {
        return player;
    }

    public Image getImage() {
        return image;
    }

    public Piece goTo(Position position) {
        Piece piece = chessboard.getChess(position);
        chessboard.remove(position);
        chessboard.remove(this.position);
        this.position = position;
        chessboard.add(this);
        return piece;
    }
    public boolean canGoTo(Position position1) {
        Piece piece1 = chessboard.getChess(position1);
        Shape shape = new Shape(position, position1);
        if (piece1 == null)
            return canMoveTo(position1, shape);
        else if (piece1.getPlayer() == player)
            return false;
        else
            return canCapture(position1, piece1, shape);
    }
    abstract protected boolean canMoveTo(Position position1, Shape shape);

    abstract protected boolean canCapture(Position position1, Piece piece1, Shape shape);

    protected boolean inPalace(Position position1) {
        if (player.isRed()) return position1.getX() >= 4 && position1.getX() <= 6 && position1.getY() >= 1 && position1.getY() <= 3;
        else                return position1.getX() >= 4 && position1.getX() <= 6 && position1.getY() >= 8 && position1.getY() <= 10;
    }

    protected boolean behindRiver(Position position) {
        if (player.isRed()) return position.getY() <= 5;
        else                return position.getY() >= 6;
    }

    protected boolean forward(Position position) {
        if (player.isRed()) return this.position.getX() == position.getX() && this.position.getY() + 1 == position.getY();
        else                return this.position.getX() == position.getX() && this.position.getY() - 1 == position.getY();
    }

    protected boolean forwardOrHorizontally(Position position) {
        if (player.isRed()) return this.position.getX() == position.getX() && this.position.getY() + 1 == position.getY() || this.position.getY() == position.getY() && (this.position.getX() + 1 == position.getX() || this.position.getX() == position.getX() + 1);
        else                return this.position.getX() == position.getX() && this.position.getY() - 1 == position.getY() || this.position.getY() == position.getY() && (this.position.getX() + 1 == position.getX() || this.position.getX() == position.getX() + 1);
    }

    protected int countInterveningPieces(Position position1) {
        int count = 0;
        Position p;
        if (position.getX() == position1.getX()) {
            for (int i = Math.min(position.getY(), position1.getY()) + 1; i < Math.max(position.getY(), position1.getY()); i++) {
                p = new Position(position.getX(), i);
                if (chessboard.getChess(p) != null) count++;
            }
            return count;
        }
        else if (position.getY() == position1.getY()) {
            for (int i = Math.min(position.getX(), position1.getX()) + 1; i < Math.max(position.getX(), position1.getX()); i++) {
                p = new Position(i, position.getY());
                if (chessboard.getChess(p) != null) count++;
            }
            return count;
        }
        return count;
    }
    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
