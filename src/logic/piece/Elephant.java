package logic.piece;

import logic.Chessboard;
import logic.Player;
import logic.Position;
import logic.Shape;

import java.awt.*;

public class Elephant extends Piece {

    public Elephant(Chessboard chessboard, Player player, Position position, Image image) {
        super(chessboard, player, position, image);
    }

    @Override
    protected boolean canMoveTo(Position position1, Shape shape) {
        if (!(shape.isShapeOf(2, 2) && behindRiver(position1))) return false; // 田字且不能过河
        return chessboard.getChess(position.leg(position1)) == null; // 没有堵象眼
    }

    @Override
    protected boolean canCapture(Position position1, Piece piece1, Shape shape) {
        if (!(shape.isShapeOf(2, 2) && behindRiver(position1))) return false; // 田字且不能过河
        return chessboard.getChess(position.leg(position1)) == null; // 没有堵象眼
    }
}
