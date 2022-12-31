package logic.piece;

import logic.Chessboard;
import logic.Player;
import logic.Position;
import logic.Shape;

import java.awt.*;
import java.util.Collection;
import java.util.HashSet;

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

    @Override
    public Collection<Position> getPositionsCanGoTo() {
        Collection<Position> positionsCanGoTo = new HashSet<>();
        Position position1;
        position1 = position.add(1, 1);
        if (canGoTo(position1) && !willBeCheckmatedWhenGoingTo(position1)) positionsCanGoTo.add(position1);
        position1 = position.add(1, -1);
        if (canGoTo(position1) && !willBeCheckmatedWhenGoingTo(position1)) positionsCanGoTo.add(position1);
        position1 = position.add(-1, -1);
        if (canGoTo(position1) && !willBeCheckmatedWhenGoingTo(position1)) positionsCanGoTo.add(position1);
        position1 = position.add(-1, 1);
        if (canGoTo(position1) && !willBeCheckmatedWhenGoingTo(position1)) positionsCanGoTo.add(position1);
        return positionsCanGoTo;
    }

    @Override
    public Piece clone(Chessboard chessboard, Position position) {
        return new Advisor(chessboard, player, position, image);
    }
}
