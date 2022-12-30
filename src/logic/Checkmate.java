package logic;

import logic.piece.General;
import logic.piece.Piece;

import java.util.Collection;

public class Checkmate {
    protected Chessboard chessboard;
    private Piece to_pie = null;

    public Checkmate(Chessboard chessboard) {
        this.chessboard = chessboard;
    }

    public int judge_status(Collection<Piece> pieces, Player player) {
        // 0：未被将军 1：被将军 2：被将死
        int status = 0;
        Position general_pos = null;
        for (Piece piece : pieces) {
            if (piece.getPlayer() == player && piece instanceof General) {
                general_pos = piece.getPosition();
                break;
            }
        }
        if (general_pos != null && ifCheck(pieces, player, general_pos))
            status = 1;
        if (status == 1) {
            status = 2;
            Position now_pos, to_pos;
            for (Piece piece : pieces) {
                if (piece.getPlayer() == player) {
                    now_pos = piece.getPosition();
                    for (int x = 1; x <= 9 && status == 2; x++)
                        for (int y = 1; y <= 10 && status == 2; y++) {
                            to_pos = new Position(x, y);
                            if (piece.canGoTo(to_pos)) {
                                Chessboard chessboard1 = chessboard.clone();
                                Piece now_pie = chessboard1.getPiece(now_pos);
                                to_pie = chessboard1.getPiece(to_pos);

                                now_pie.goTo(to_pos);
                                if (!ifCheck(chessboard1.getPieces(), player,
                                        now_pie instanceof General ? to_pos : general_pos))
                                    status = 1;
                                to_pie = null;
                            }
                        }
                }
            }
        }
        return status;
    }

    public boolean ifCheck(Collection<Piece> pieces, Player player, Position general_pos) {
        for (Piece piece : pieces) {
            if (piece.getPlayer() != player && piece != to_pie) {
                if (piece.canGoTo(general_pos))
                    return true;
            }
        }
        return false;
    }

}
