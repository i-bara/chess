package logic.piece;

import logic.Chessboard;
import logic.Player;
import logic.Position;
import logic.Shape;

import java.awt.*;

public class Soldier extends Piece{

    public Soldier(Chessboard chessboard, Player player, Position position, Image image) {
        super(chessboard, player, position, image);
    }

    @Override
    protected boolean canMoveTo(Position position1, Shape shape) {
        if (behindRiver(position))  return forward(position1);
        else                        return forwardOrHorizontally(position1);
    }

    @Override
    protected boolean canCapture(Position position1, Piece piece1, Shape shape) {
        if (behindRiver(position))  return forward(position1);
        else                        return forwardOrHorizontally(position1);
    }
}
