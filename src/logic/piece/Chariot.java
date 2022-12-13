package logic.piece;

import logic.Chessboard;
import logic.Player;
import logic.Position;
import logic.Shape;

import java.awt.*;

public class Chariot extends Piece {

    public Chariot(Chessboard chessboard, Player player, Position position, Image image) {
        super(chessboard, player, position, image);
    }

    @Override
    protected boolean canMoveTo(Position position1, Shape shape) {
        return shape.getMin() == 0 && countInterveningPieces(position1) == 0;
    }

    @Override
    protected boolean canCapture(Position position1, Piece piece1, Shape shape) {
        return shape.getMin() == 0 && countInterveningPieces(position1) == 0;
    }
}
