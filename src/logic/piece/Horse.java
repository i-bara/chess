package logic.piece;

import logic.Chessboard;
import logic.Player;
import logic.Position;
import logic.Shape;

import java.awt.*;
import java.util.Collection;
import java.util.HashSet;

public class Horse extends Piece {

    public Horse(Chessboard chessboard, Player player, Position position, Image image) {
        super(chessboard, player, position, image);
    }

    @Override
    protected boolean canMoveTo(Position position1, Shape shape) {
        if (!(shape.isShapeOf(1, 2))) return false; // 日字
        return chessboard.getPiece(position.leg(position1)) == null; // 没有绊马腿
    }

    @Override
    protected boolean canCapture(Position position1, Piece piece1, Shape shape) {
        if (!(shape.isShapeOf(1, 2))) return false; // 日字
        return chessboard.getPiece(position.leg(position1)) == null; // 没有绊马腿
    }

    @Override
    public Collection<Position> getPositionsCanGoTo() {
        Collection<Position> positionsCanGoTo = new HashSet<>();
        Position position1;
        position1 = position.add(1, 2);
        if (canGoTo(position1) && !willBeCheckmatedWhenGoingTo(position1)) positionsCanGoTo.add(position1);
        position1 = position.add(2, 1);
        if (canGoTo(position1) && !willBeCheckmatedWhenGoingTo(position1)) positionsCanGoTo.add(position1);
        position1 = position.add(2, -1);
        if (canGoTo(position1) && !willBeCheckmatedWhenGoingTo(position1)) positionsCanGoTo.add(position1);
        position1 = position.add(1, -2);
        if (canGoTo(position1) && !willBeCheckmatedWhenGoingTo(position1)) positionsCanGoTo.add(position1);
        position1 = position.add(-1, -2);
        if (canGoTo(position1) && !willBeCheckmatedWhenGoingTo(position1)) positionsCanGoTo.add(position1);
        position1 = position.add(-2, -1);
        if (canGoTo(position1) && !willBeCheckmatedWhenGoingTo(position1)) positionsCanGoTo.add(position1);
        position1 = position.add(-2, 1);
        if (canGoTo(position1) && !willBeCheckmatedWhenGoingTo(position1)) positionsCanGoTo.add(position1);
        position1 = position.add(-1, 2);
        if (canGoTo(position1) && !willBeCheckmatedWhenGoingTo(position1)) positionsCanGoTo.add(position1);
        return positionsCanGoTo;
    }

    @Override
    public Piece clone(Chessboard chessboard, Position position) {
        return new Horse(chessboard, player, position, image);
    }
}
