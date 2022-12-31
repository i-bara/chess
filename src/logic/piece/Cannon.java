package logic.piece;

import logic.Chessboard;
import logic.Player;
import logic.Position;
import logic.Shape;

import java.awt.*;
import java.util.Collection;
import java.util.HashSet;

public class Cannon extends Piece{

    public Cannon(Chessboard chessboard, Player player, Position position, Image image) {
        super(chessboard, player, position, image);
    }

    @Override
    protected boolean canMoveTo(Position position1, Shape shape) {
        return shape.getMin() == 0 && countInterveningPieces(position1) == 0;
    }

    @Override
    protected boolean canCapture(Position position1, Piece piece1, Shape shape) {
        return shape.getMin() == 0 && countInterveningPieces(position1) == 1;
    }

    @Override
    public Collection<Position> getPositionsCanGoTo() {
        Collection<Position> positionsCanGoTo = new HashSet<>();
        Position position1;
        for (int i = -8; i <= 8; i++) {
            position1 = position.add(i, 0);
            if (canGoTo(position1) && !willBeCheckmatedWhenGoingTo(position1)) positionsCanGoTo.add(position1);
        }
        for (int i = -9; i <= 9; i++) {
            position1 = position.add(0, i);
            if (canGoTo(position1) && !willBeCheckmatedWhenGoingTo(position1)) positionsCanGoTo.add(position1);
        }
        return positionsCanGoTo;
    }

    @Override
    public Piece clone(Chessboard chessboard, Position position) {
        return new Cannon(chessboard, player, position, image);
    }
}
