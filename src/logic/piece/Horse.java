package logic.piece;

import logic.Chessboard;
import logic.Player;
import logic.Position;
import logic.Shape;

import java.awt.*;

public class Horse extends Piece {

    public Horse(Chessboard chessboard, Player player, Position position, Image image) {
        super(chessboard, player, position, image);
    }

    @Override
    protected boolean canMoveTo(Position position1, Shape shape) {
        if (!(shape.isShapeOf(1, 2))) return false; // 日字
        return chessboard.getChess(position.leg(position1)) == null; // 没有绊马腿
    }

    @Override
    protected boolean canCapture(Position position1, Piece piece1, Shape shape) {
        if (!(shape.isShapeOf(1, 2))) return false; // 日字
        return chessboard.getChess(position.leg(position1)) == null; // 没有绊马腿
    }
}
