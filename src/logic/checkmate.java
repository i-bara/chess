package logic;

import logic.piece.General;
import logic.piece.Piece;

import java.util.HashSet;

public class checkmate {
    protected Chessboard chessboard;
    private int status = 0; // 0��δ������ 1�������� 2��������
    private Piece to_pie = null;

    public checkmate(Chessboard chessboard) {
        this.chessboard = chessboard;
    }

    public int judge_status(HashSet<Piece> pieces, Player player) {
        status = 0;
        Position general_pos = null;
        for (Piece piece : pieces) {
            if (piece.getPlayer() == player && piece.getClass() == General.class) {
                general_pos = piece.getPosition();
                break;
            }
        }
        if (general_pos != null && ifcheck(pieces, player, general_pos))
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
                                to_pie = chessboard.getChess(to_pos);

                                chessboard.remove(piece.getPosition());
                                piece.setPosition(to_pos);
                                chessboard.add(piece);
                                if (!ifcheck(pieces, player, general_pos))
                                    status = 1;
                                chessboard.remove(piece.getPosition());
                                piece.setPosition(now_pos);
                                chessboard.add(piece);
                                to_pie = null;
                            }
                        }
                }
            }
        }
        return status;
    }

    public boolean ifcheck(HashSet<Piece> pieces, Player player, Position general_pos) {
        for (Piece piece : pieces) {
            if (piece.getPlayer() != player && piece != to_pie) {
                if (piece.canGoTo(general_pos))
                    return true;
            }
        }
        return false;
    }

}
