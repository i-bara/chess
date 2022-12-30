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
        Position general_pos;
        general_pos = chessboard.getGeneralPosition(player);
        if (general_pos != null && chessboard.ifCheck(player))
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
                                if (!piece.willBeCheckmatedWhenGoingTo(to_pos)) status = 1;
                            }
                        }
                }
            }
        }
        return status;
    }

}
