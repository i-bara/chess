package logic;

import logic.piece.Piece;

public class Move {

    private final Piece piece;
    private final Position position;

    Move(Piece piece, Position position) {
        this.piece = piece;
        this.position = position;
    }

    public Piece getPiece() {
        return piece;
    }

    public Position getPosition() {
        return position;
    }

    public String toString() {
        return piece.toString() + " " + piece.getPosition().toString() + "->" + position.toString();
    }
}
