package logic;

import logic.piece.Piece;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Chessboard {
    private Map<Position, Piece> m ;

    public Chessboard() {
        m = new HashMap<>(){
            @Override
            public boolean isEmpty() {
                return super.isEmpty();
            }
        };
    }

    public void add(Piece piece) {
        m.put(piece.getPosition(), piece);
    }

    public Piece getPiece(Position position) {
        return m.get(position);
    }

    public Collection<Piece> getPieces() {
        return m.values();
    }

    public void remove(Position position) {
        m.remove(position);
    }

    public Chessboard clone() {
        Chessboard chessboard = new Chessboard();
        Map<Position, Piece> m = new HashMap<>(){
            @Override
            public boolean isEmpty() {
                return super.isEmpty();
            }
        };
        for (Position position : this.m.keySet()) {
            //m.put(position, this.m.get(position));
            m.put(position, this.m.get(position).clone(chessboard, position));
        }
        chessboard.m = m;
        return chessboard;
    }
}
