package logic.piece;

import logic.Chessboard;
import logic.Player;
import logic.Position;
import logic.Shape;

import java.awt.*;

public class General extends Piece {

    public General(Chessboard chessboard, Player player, Position position, Image image) {
        super(chessboard, player, position, image);
    }

    @Override
    protected boolean canMoveTo(Position position1, Shape shape) {
        return shape.isShapeOf(0, 1) && inPalace(position1);
    }

    @Override
    protected boolean canCapture(Position position1, Piece piece1, Shape shape) {
        return shape.isShapeOf(0, 1) && inPalace(position1) ||
                position.getX() == position1.getX()
                        && piece1.getClass() == General.class
                        && countInterveningPieces(position1) == 0;
    }

    @Override
    public Piece clone(Chessboard chessboard, Position position) {
        return new General(chessboard, player, position, image);
    }
}
