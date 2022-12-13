package logic;

import logic.piece.Piece;

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

    public Piece getChess(Position position) {
        return m.get(position);
    }

    public void remove(Position position) {
        m.remove(position);
    }

}
