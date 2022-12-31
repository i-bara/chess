package logic;

import logic.piece.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
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
    public boolean ifChecked(Player player) {
        Position generalPosition = getGeneralPosition(player);
        if (generalPosition == null) return false;
        for (Piece piece : getPieces()) {
            if (piece.getPlayer() != player) {
                if (piece.canGoTo(generalPosition))
                    return true;
            }
        }
        return false;
    }

    public boolean ifCheckmated(Player player) {
        Collection<Move> possibleMoves = getPossibleMoves(player);
        return possibleMoves.isEmpty();
    }

    private Collection<Position> getPositionsCanGoTo(Piece piece) {
        return piece.getPositionsCanGoTo();
    }

    private Collection<Move> getPossibleMoves(Player player) {
        Collection<Move> possibleMoves = new HashSet<>();
        Collection<Piece> pieces = new HashSet<>(getPieces());
        for (Piece piece : pieces) {
            if (piece.getPlayer() == player) {
                Collection<Position> positionsCanGoTo = getPositionsCanGoTo(piece);
                for (Position position : positionsCanGoTo) {
                    possibleMoves.add(new Move(piece, position));
                }
            }
        }
        return possibleMoves;
    }

    private Collection<Move> getPossibleMovesByOpponent(Player player) {
        Collection<Move> possibleMoves = new HashSet<>();
        Collection<Piece> pieces = new HashSet<>(getPieces());
        for (Piece piece : pieces) {
            if (piece.getPlayer() != player) {
                Collection<Position> positionsCanGoTo = getPositionsCanGoTo(piece);
                for (Position position : positionsCanGoTo) {
                    possibleMoves.add(new Move(piece, position));
                }
            }
        }
        return possibleMoves;
    }

    public void move(Move move) {
        move.getPiece().goTo(move.getPosition());
    }

    private static final float GENERAL = 10000;
    private static final float CHARIOT = 100;
    private static final float CANNON = 67;
    private static final float HORSE = 50;
    private static final float ELEPHANT = 30;
    private static final float ADVISOR = 30;
    private static final float SOLDIER_BEHIND_THE_RIVER = 10;
    private static final float SOLDIER_CROSSED_THE_RIVER = 30;

    private float accessMove(Move move) {
        Piece piece = getPiece(move.getPosition());
        if (piece == null) return 0;
        else if (piece instanceof General) return GENERAL;
        else if (piece instanceof Chariot) return CHARIOT;
        else if (piece instanceof Cannon) return CANNON;
        else if (piece instanceof Horse) return HORSE;
        else if (piece instanceof Elephant) return ELEPHANT;
        else if (piece instanceof Advisor) return ADVISOR;
        else if (piece instanceof Soldier && piece.behindRiver(piece.getPosition())) return SOLDIER_BEHIND_THE_RIVER;
        else if (piece instanceof Soldier && !piece.behindRiver(piece.getPosition())) return SOLDIER_CROSSED_THE_RIVER;
        return 0;
    }

    private float accessMoveDeep(Chessboard chessboard, Player player, Move move, int depth) {
        if (depth == 0) return 0;
        Chessboard chessboard1 = chessboard.clone();
        Move move1 = new Move(chessboard1.getPiece(move.getPiece().getPosition()), move.getPosition());
        chessboard1.move(move1);

//        Piece piece = move.getPiece();
//        Position position = piece.getPosition();
//        Position position1 = move.getPosition();
//        Piece piece1 = chessboard.getPiece(position1);
//        piece.goTo(position1);
//        float score = chessboard.accessMove(move) - chessboard1.getGreatestScoreByOpponent(player, depth - 1);
//        chessboard.remove(position1);
//        piece.setPosition(position);
//        chessboard.add(piece);
//        if (piece1 != null) chessboard.add(piece1);

        return chessboard.accessMove(move) - chessboard1.getGreatestScoreByOpponent(player, depth - 1);
    }

    private float accessMoveDeepByOpponent(Chessboard chessboard, Player player, Move move, int depth) {
        if (depth == 0) return 0;
        Chessboard chessboard1 = chessboard.clone();
        Move move1 = new Move(chessboard1.getPiece(move.getPiece().getPosition()), move.getPosition());
        chessboard1.move(move1);

        return chessboard.accessMove(move) - chessboard1.getGreatestScore(player, depth - 1);
    }

    private float getGreatestScore(Player player, int n) {
        Collection<Move> possibleMoves = getPossibleMoves(player);
        float max = -1000000;
        for (Move move : possibleMoves) {
            if (accessMoveDeep(this, player, move, n) > max) {
                max = accessMoveDeep(this, player, move, n);
            }
        }
        return max;
    }

    private float getGreatestScoreByOpponent(Player player, int n) {
        Collection<Move> possibleMovesByOpponent = getPossibleMovesByOpponent(player);
        float max = -1000000;
        for (Move move : possibleMovesByOpponent) {
            if (accessMoveDeepByOpponent(this, player, move, n) > max) {
                max = accessMoveDeepByOpponent(this, player, move, n);
            }
        }
        return max;
    }

    public Move getBestMove(Player player, int n) {
        Collection<Move> possibleMoves = getPossibleMoves(player);
        System.out.println(possibleMoves.size());
        float max = -100000000;
        Move move_max = null;
        for (Move move : possibleMoves) {
            float score = accessMoveDeep(this, player, move, n);
            System.out.println(move + " " + score);
            if (max < score) {
                max = score;
                move_max = move;
            }
        }
        System.out.println(move_max);
        return move_max;
    }
}
