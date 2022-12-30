package logic;

import logic.piece.General;
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

    /**
     * 克隆一个棋盘，注意所有棋子也会被克隆，因此克隆棋盘后的判断请使用「克隆后的棋盘上的棋子」
     * @return 克隆后的棋盘
     */
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

    /**
     * 获取某玩家将军位置
     * @param player 玩家
     * @return 该玩家将军的位置
     */
    public Position getGeneralPosition(Player player) {
        for (Piece piece : getPieces()) {
            if (piece.getPlayer() == player && piece instanceof General) {
                return piece.getPosition();
            }
        }
        return null;
    }

    /**
     * 判断当前某玩家是否被将军
     * @param player 玩家
     * @return 该玩家正在被将军
     */
    public boolean ifCheck(Player player) {
        Position generalPosition = getGeneralPosition(player);
        for (Piece piece : getPieces()) {
            if (piece.getPlayer() != player) {
                if (piece.canGoTo(generalPosition))
                    return true;
            }
        }
        return false;
    }
}
