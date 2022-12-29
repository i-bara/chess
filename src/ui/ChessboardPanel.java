package ui;

import logic.Chessboard;
import logic.Player;
import logic.Position;
import logic.piece.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.nio.channels.UnresolvedAddressException;
import java.util.HashSet;
import java.util.Objects;

/**
 * 棋盘 panel。
 * 同时根据点击事件控制游戏。
 * @version 2022.11.30 19:57
 */
public class ChessboardPanel extends JPanel {

    private static final URL iconURL = ChessboardPanel.class.getResource("../images/chessboard.png");
    private final ImageIcon icon;
    private final Image image;

    private final HashSet<Piece> pieces;

    private final Chessboard chessboard;
    private final Player redPlayer;
    private final Player blackPlayer;

    private Player turnPlayer;

    private Position focusPosition;
    private Piece focusPiece;
    private Position focusPositionToMove;

    private int focusStatus;

    // 依据鼠标的按下与释放，有四种状态
    private static final int NO_FOCUS = 0; // 尚未按下鼠标
    private static final int TO_FOCUS = 1; // 按下鼠标，但尚未确认是否这个棋子（因为需要释放的时候也在同一棋子上）
    private static final int FOCUSED = 2; // 松开鼠标，棋子被拾起（但不知道能否移动到相应的位置）
    private static final int TO_MOVE = 3; // 按下鼠标，但尚未确认是否要移动（因为需要释放的时候也在同一棋子上）

    // 每次重新绘制只绘制一个矩形内的棋子，repaintPosition1 是该矩形的左上角，repaintPosition2 是该矩形的右下角。
    private boolean isRepainting = false; // 用来判断是否重新绘制
    private Position repaintPosition1;
    private Position repaintPosition2;

    ChessboardPanel(LayoutManager layoutManager) {
        super(layoutManager);
        pieces = new HashSet<>();
        if (iconURL != null) {
            icon = new ImageIcon(iconURL);
            image = icon.getImage();
        }
        else throw new UnresolvedAddressException();

        chessboard = new Chessboard();
        redPlayer = new Player(true);
        blackPlayer = new Player(false);

        initializeChessboard();
        turnPlayer = redPlayer;

        addMouseListener(new MouseAdapter() {

            private Position position;
            private Piece piece;
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                position = getPosition(e.getPoint());
                piece = chessboard.getChess(position);
                if (position != null && !isRepainting) { // 重新绘制完成之后才允许新的鼠标事件，防止重新绘制前信息被改变
                    if (focusStatus == NO_FOCUS || focusStatus == TO_FOCUS) {
                        focusPosition = position;
                        focusPiece = piece;
                        focusStatus = TO_FOCUS;
                    }
                    else if (focusStatus == FOCUSED || focusStatus == TO_MOVE) {
                        focusPositionToMove = position;
                        focusStatus = TO_MOVE;
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                position = getPosition(e.getPoint());
                piece = chessboard.getChess(position);
                if (position != null) {
                    if (focusStatus == TO_FOCUS) {
                        if (piece == null) {
                            focusPosition = null;
                            focusPiece = null;
                            focusStatus = NO_FOCUS;
                        }
                        else if (position.equals(focusPosition) && piece.getPlayer() == turnPlayer) {
                            focusStatus = FOCUSED;
                        }
                        else {
                            focusPosition = null;
                            focusPiece = null;
                            focusStatus = NO_FOCUS;
                        }
                    }
                    else if (focusStatus == TO_MOVE) {
                        if (position.equals(focusPositionToMove)) {
                            // 如果 focusPiece 可以从 focusPosition 移动到 focusPositionToMove 对应的操作
                            if (focusPiece.canGoTo(position)) {
                                focusPiece.goTo(position);
                                pieces.remove(piece);

                                // 下一回合是另一个人着棋
                                if (turnPlayer == redPlayer) turnPlayer = blackPlayer;
                                else if (turnPlayer == blackPlayer) turnPlayer = redPlayer;

                                repaint(getPoint(focusPosition).x, getPoint(focusPosition).y,
                                        getPieceSize(), getPieceSize());
                                repaint(getPoint(focusPositionToMove).x, getPoint(focusPositionToMove).y,
                                        getPieceSize(), getPieceSize());
                                isRepainting = true;
                                repaintPosition1 = new Position(Math.min(focusPosition.getX(), focusPositionToMove.getX()),
                                        Math.min(focusPosition.getY(), focusPositionToMove.getY()));
                                repaintPosition2 = new Position(Math.max(focusPosition.getX(), focusPositionToMove.getX()),
                                        Math.max(focusPosition.getY(), focusPositionToMove.getY()));
                                // 每一次鼠标事件结束后将调用 paintComponent()

                                focusStatus = NO_FOCUS;
                                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                            }
                            else if (piece == null) {
                                focusPosition = null;
                                focusPiece = null;
                                focusStatus = NO_FOCUS;
                                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                            }
                            else if (focusPiece.getPlayer() == piece.getPlayer() && focusPiece != piece) {
                                focusPosition = position;
                                focusPiece = piece;
                                focusStatus = FOCUSED;
                            }
                            else {
                                focusPosition = null;
                                focusPiece = null;
                                focusStatus = NO_FOCUS;
                                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                            }
                        }
                        else {
                            focusPosition = null;
                            focusPiece = null;
                            focusStatus = NO_FOCUS;
                            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                        }
                    }
                }
                else {
                    focusPosition = null;
                    focusPiece = null;
                    focusStatus = NO_FOCUS;
                    setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                }
            }
        });
        addMouseMotionListener(new MouseAdapter() {

            @Override
            public void mouseMoved(MouseEvent e) {
                super.mouseMoved(e);
                Position position = getPosition(e.getPoint());
                Piece piece = chessboard.getChess(position);
                if (focusStatus == NO_FOCUS || focusStatus == TO_FOCUS) {
                    if (piece == null) setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                    else if (piece.getPlayer() == turnPlayer) setCursor(new Cursor(Cursor.HAND_CURSOR));
                    else setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                }
                else if (focusStatus == FOCUSED || focusStatus == TO_MOVE) {
                    if (position != null) setCursor(new Cursor(Cursor.HAND_CURSOR));
                    else setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                }
            }
        });

    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // 缩放图片以适应高度
        Image scaledImage = image.getScaledInstance(-1, getHeight(), Image.SCALE_AREA_AVERAGING);

        //g.drawImage(scaledImage, 0, 0, this);
        // 绘制图片
        icon.setImage(scaledImage);
        icon.paintIcon(this, g, 0, 0);
        if (isRepainting) {
            for (Piece piece: pieces) {
                if (piece.getPosition().getX() >= repaintPosition1.getX() && piece.getPosition().getY() >= repaintPosition1.getY() &&
                        piece.getPosition().getX() <= repaintPosition2.getX() && piece.getPosition().getY() <= repaintPosition2.getY()) {
                    Image scaled = piece.getImage().getScaledInstance(-1, getPieceSize(), Image.SCALE_AREA_AVERAGING);
                    icon.setImage(scaled);
                    icon.paintIcon(this, g, getPoint(piece.getPosition()).x, getPoint(piece.getPosition()).y);
                }
            }
            isRepainting = false;
        } else {
            for (Piece piece: pieces) {
                Image scaled = piece.getImage().getScaledInstance(-1, getPieceSize(), Image.SCALE_AREA_AVERAGING);
                icon.setImage(scaled);
                icon.paintIcon(this, g, getPoint(piece.getPosition()).x, getPoint(piece.getPosition()).y);
                // new ImageIcon(Objects.requireNonNull(ChessboardPanel.class.getResource("../images/icon.png"))).getImage()
            }
        }
    }

    public ChessboardPanel addPiece(Piece piece) {
        pieces.add(piece);
        return this;
    }

    /**
     * 从棋盘坐标到画布位置的映射
     * @param position 棋盘的坐标
     * @return 要在画布上绘制的位置
     */
    public Point getPoint(Position position) {
        return new Point((int)(0.027 * getHeight() + position.getX() * 0.0777 * getHeight()),
                (int)(0.88 * getHeight() - position.getY() * 0.0777 * getHeight()));
    }

    /**
     * 从画布位置到棋盘坐标的映射
     * @param point 在画布上点击的位置
     * @return 对应的棋盘的坐标
     */
    private Position getPosition(Point point) {
        int x = (int)((((float)point.x / getHeight()) - 0.027) / 0.0777);
        int y = (int)((0.88 - ((float)point.y / getHeight())) / 0.0777) + 1;
        Position position = new Position(x, y);
        Point point1 = new Point(getPoint(position).x + getPieceSize() / 2,
                getPoint(position).y + getPieceSize() / 2);
        if (point.distance(point1) <= 0.05 * getHeight()) return position;
        else return null;
    }

    /**
     * 在游戏开始时，把所有棋子加入棋盘
     */
    private void initializeChessboard() {
        addPiece(new General(chessboard, redPlayer, new Position(5, 1),
                new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                        .getResource("../images/red_general.png"))).getImage()))
                .addPiece(new Advisor(chessboard, redPlayer, new Position(4, 1),
                        new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                                .getResource("../images/red_advisor.png"))).getImage()))
                .addPiece(new Advisor(chessboard, redPlayer, new Position(6, 1),
                        new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                                .getResource("../images/red_advisor.png"))).getImage()))
                .addPiece(new Elephant(chessboard, redPlayer, new Position(3, 1),
                        new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                                .getResource("../images/red_elephant.png"))).getImage()))
                .addPiece(new Elephant(chessboard, redPlayer, new Position(7, 1),
                        new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                                .getResource("../images/red_elephant.png"))).getImage()))
                .addPiece(new Horse(chessboard, redPlayer, new Position(2, 1),
                        new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                                .getResource("../images/red_horse.png"))).getImage()))
                .addPiece(new Horse(chessboard, redPlayer, new Position(8, 1),
                        new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                                .getResource("../images/red_horse.png"))).getImage()))
                .addPiece(new Chariot(chessboard, redPlayer, new Position(1, 1),
                        new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                                .getResource("../images/red_chariot.png"))).getImage()))
                .addPiece(new Chariot(chessboard, redPlayer, new Position(9, 1),
                        new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                                .getResource("../images/red_chariot.png"))).getImage()))
                .addPiece(new Cannon(chessboard, redPlayer, new Position(2, 3),
                        new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                                .getResource("../images/red_cannon.png"))).getImage()))
                .addPiece(new Cannon(chessboard, redPlayer, new Position(8, 3),
                        new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                                .getResource("../images/red_cannon.png"))).getImage()))
                .addPiece(new Soldier(chessboard, redPlayer, new Position(1, 4),
                        new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                                .getResource("../images/red_soldier.png"))).getImage()))
                .addPiece(new Soldier(chessboard, redPlayer, new Position(3, 4),
                        new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                                .getResource("../images/red_soldier.png"))).getImage()))
                .addPiece(new Soldier(chessboard, redPlayer, new Position(5, 4),
                        new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                                .getResource("../images/red_soldier.png"))).getImage()))
                .addPiece(new Soldier(chessboard, redPlayer, new Position(7, 4),
                        new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                                .getResource("../images/red_soldier.png"))).getImage()))
                .addPiece(new Soldier(chessboard, redPlayer, new Position(9, 4),
                        new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                                .getResource("../images/red_soldier.png"))).getImage()))
                .addPiece(new General(chessboard, blackPlayer, new Position(5, 10),
                        new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                                .getResource("../images/black_general.png"))).getImage()))
                .addPiece(new Advisor(chessboard, blackPlayer, new Position(6, 10),
                        new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                                .getResource("../images/black_advisor.png"))).getImage()))
                .addPiece(new Advisor(chessboard, blackPlayer, new Position(4, 10),
                        new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                                .getResource("../images/black_advisor.png"))).getImage()))
                .addPiece(new Elephant(chessboard, blackPlayer, new Position(7, 10),
                        new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                                .getResource("../images/black_elephant.png"))).getImage()))
                .addPiece(new Elephant(chessboard, blackPlayer, new Position(3, 10),
                        new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                                .getResource("../images/black_elephant.png"))).getImage()))
                .addPiece(new Horse(chessboard, blackPlayer, new Position(8, 10),
                        new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                                .getResource("../images/black_horse.png"))).getImage()))
                .addPiece(new Horse(chessboard, blackPlayer, new Position(2, 10),
                        new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                                .getResource("../images/black_horse.png"))).getImage()))
                .addPiece(new Chariot(chessboard, blackPlayer, new Position(9, 10),
                        new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                                .getResource("../images/black_chariot.png"))).getImage()))
                .addPiece(new Chariot(chessboard, blackPlayer, new Position(1, 10),
                        new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                                .getResource("../images/black_chariot.png"))).getImage()))
                .addPiece(new Cannon(chessboard, blackPlayer, new Position(8, 8),
                        new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                                .getResource("../images/black_cannon.png"))).getImage()))
                .addPiece(new Cannon(chessboard, blackPlayer, new Position(2, 8),
                        new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                                .getResource("../images/black_cannon.png"))).getImage()))
                .addPiece(new Soldier(chessboard, blackPlayer, new Position(9, 7),
                        new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                                .getResource("../images/black_soldier.png"))).getImage()))
                .addPiece(new Soldier(chessboard, blackPlayer, new Position(7, 7),
                        new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                                .getResource("../images/black_soldier.png"))).getImage()))
                .addPiece(new Soldier(chessboard, blackPlayer, new Position(5, 7),
                        new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                                .getResource("../images/black_soldier.png"))).getImage()))
                .addPiece(new Soldier(chessboard, blackPlayer, new Position(3, 7),
                        new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                                .getResource("../images/black_soldier.png"))).getImage()))
                .addPiece(new Soldier(chessboard, blackPlayer, new Position(1, 7),
                        new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                                .getResource("../images/black_soldier.png"))).getImage()));
    }

    /**
     * 用来获取棋子在画布上绘制的尺寸
     * @return 当前棋子绘制在画布上的宽与高
     */
    private int getPieceSize() {
        return (int)(0.09 * getHeight());
    }

}
