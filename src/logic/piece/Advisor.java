package logic.piece;

import logic.Chessboard;
import logic.Player;
import logic.Position;
import logic.Shape;

import java.awt.*;

public class Advisor extends Piece {

    public Advisor(Chessboard chessboard, Player player, Position position, Image image) {
        super(chessboard, player, position, image);
    }

    @Override
    protected boolean canMoveTo(Position position1, Shape shape) {
        return shape.isShapeOf(1, 1) && inPalace(position1);
    }

    @Override
    protected boolean canCapture(Position position1, Piece piece1, Shape shape) {
        return shape.isShapeOf(1, 1) && inPalace(position1);
    }
}
