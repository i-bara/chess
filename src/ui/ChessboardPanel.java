package ui;
import logic.Checkmate;
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
import java.util.ArrayList;
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

    private Chessboard chessboard;
    private Player redPlayer;
    private Player blackPlayer;

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

    // 记忆过去的步骤
    private ArrayList<Chessboard> status;
    private int step;

    ChessboardPanel(LayoutManager layoutManager) {
        super(layoutManager);



        // 获取棋盘图像
        if (iconURL == null) throw new UnresolvedAddressException();
        icon = new ImageIcon(iconURL);
        image = icon.getImage();



        // 初始化棋盘与棋手
        restart();



        // 添加鼠标事件监听器
        addMouseListener(new MouseAdapter() {

            private Position position;
            private Piece piece;
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                position = getPosition(e.getPoint());
                piece = chessboard.getPiece(position);
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
                piece = chessboard.getPiece(position);
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

                                if (focusPiece.willBeCheckmatedWhenGoingTo(position)) {

                                    JOptionPane.showMessageDialog(ChessboardPanel.this,
                                            "不能这样走，否则就输了。");

                                } else {
                                    focusPiece.goTo(position);

                                    while (status.size() > step + 1) {
                                        status.remove(status.size() - 1);
                                    }
                                    status.add(chessboard.clone());
                                    step++;

                                    // 下一回合是另一个人着棋
                                    if (turnPlayer == redPlayer) turnPlayer = blackPlayer;
                                    else if (turnPlayer == blackPlayer) turnPlayer = redPlayer;

                                    // 判断死否将死
                                    Chessboard chessboard1 = chessboard.clone();
                                    Checkmate checkmate = new Checkmate(chessboard1);
                                    int res = checkmate.judge_status(chessboard1.getPieces(), turnPlayer);
                                    if (res == 1)
                                        JOptionPane.showMessageDialog(ChessboardPanel.this, "将军！");
                                    else if (res == 2)
                                        JOptionPane.showMessageDialog(ChessboardPanel.this, "游戏结束！"
                                                + (turnPlayer == redPlayer ? "黑方" : "红方") + "胜利");

                                    // 加上一些数字，否则旁边的棋子会被削掉一小块
                                    repaint(getPoint(focusPosition).x+2, getPoint(focusPosition).y+2,
                                            getPieceSize()-4, getPieceSize()-4);
                                    repaint(getPoint(focusPositionToMove).x+2, getPoint(focusPositionToMove).y+2,
                                            getPieceSize()-4, getPieceSize()-4);
                                    isRepainting = true;
                                    repaintPosition1 = new Position(
                                            Math.min(focusPosition.getX(), focusPositionToMove.getX()),
                                            Math.min(focusPosition.getY(), focusPositionToMove.getY()));
                                    repaintPosition2 = new Position(
                                            Math.max(focusPosition.getX(), focusPositionToMove.getX()),
                                            Math.max(focusPosition.getY(), focusPositionToMove.getY()));
                                    // 每一次鼠标事件结束后将调用 paintComponent()

                                    focusStatus = NO_FOCUS;
                                    setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                                }
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
                Piece piece = chessboard.getPiece(position);
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
            for (Piece piece: chessboard.getPieces()) {
                if (piece.getPosition().getX() >= repaintPosition1.getX() &&
                        piece.getPosition().getY() >= repaintPosition1.getY() &&
                        piece.getPosition().getX() <= repaintPosition2.getX() &&
                        piece.getPosition().getY() <= repaintPosition2.getY()) {
                    Image scaled = piece.getImage().getScaledInstance(-1, getPieceSize(),
                            Image.SCALE_AREA_AVERAGING);
                    icon.setImage(scaled);
                    icon.paintIcon(this, g, getPoint(piece.getPosition()).x, getPoint(piece.getPosition()).y);
                }
            }
            isRepainting = false;
        } else {
            for (Piece piece: chessboard.getPieces()) {
                Image scaled = piece.getImage().getScaledInstance(-1, getPieceSize(),
                        Image.SCALE_AREA_AVERAGING);
                icon.setImage(scaled);
                icon.paintIcon(this, g, getPoint(piece.getPosition()).x, getPoint(piece.getPosition()).y);
            }
        }
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
     * @return 对应的棋盘的坐标，如果离任一坐标都很远，则返回 null
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
        new General(chessboard, redPlayer, new Position(5, 1),
                new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                        .getResource("../images/red_general.png"))).getImage());
        new Advisor(chessboard, redPlayer, new Position(4, 1),
                new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                        .getResource("../images/red_advisor.png"))).getImage());
        new Advisor(chessboard, redPlayer, new Position(6, 1),
                new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                        .getResource("../images/red_advisor.png"))).getImage());
        new Elephant(chessboard, redPlayer, new Position(3, 1),
                new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                        .getResource("../images/red_elephant.png"))).getImage());
        new Elephant(chessboard, redPlayer, new Position(7, 1),
                new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                        .getResource("../images/red_elephant.png"))).getImage());
        new Horse(chessboard, redPlayer, new Position(2, 1),
                new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                        .getResource("../images/red_horse.png"))).getImage());
        new Horse(chessboard, redPlayer, new Position(8, 1),
                new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                        .getResource("../images/red_horse.png"))).getImage());
        new Chariot(chessboard, redPlayer, new Position(1, 1),
                new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                        .getResource("../images/red_chariot.png"))).getImage());
        new Chariot(chessboard, redPlayer, new Position(9, 1),
                new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                        .getResource("../images/red_chariot.png"))).getImage());
        new Cannon(chessboard, redPlayer, new Position(2, 3),
                new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                        .getResource("../images/red_cannon.png"))).getImage());
        new Cannon(chessboard, redPlayer, new Position(8, 3),
                new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                        .getResource("../images/red_cannon.png"))).getImage());
        new Soldier(chessboard, redPlayer, new Position(1, 4),
                new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                        .getResource("../images/red_soldier.png"))).getImage());
        new Soldier(chessboard, redPlayer, new Position(3, 4),
                new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                        .getResource("../images/red_soldier.png"))).getImage());
        new Soldier(chessboard, redPlayer, new Position(5, 4),
                new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                        .getResource("../images/red_soldier.png"))).getImage());
        new Soldier(chessboard, redPlayer, new Position(7, 4),
                new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                        .getResource("../images/red_soldier.png"))).getImage());
        new Soldier(chessboard, redPlayer, new Position(9, 4),
                new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                        .getResource("../images/red_soldier.png"))).getImage());
        new General(chessboard, blackPlayer, new Position(5, 10),
                new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                        .getResource("../images/black_general.png"))).getImage());
        new Advisor(chessboard, blackPlayer, new Position(6, 10),
                new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                        .getResource("../images/black_advisor.png"))).getImage());
        new Advisor(chessboard, blackPlayer, new Position(4, 10),
                new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                        .getResource("../images/black_advisor.png"))).getImage());
        new Elephant(chessboard, blackPlayer, new Position(7, 10),
                new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                        .getResource("../images/black_elephant.png"))).getImage());
        new Elephant(chessboard, blackPlayer, new Position(3, 10),
                new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                        .getResource("../images/black_elephant.png"))).getImage());
        new Horse(chessboard, blackPlayer, new Position(8, 10),
                new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                        .getResource("../images/black_horse.png"))).getImage());
        new Horse(chessboard, blackPlayer, new Position(2, 10),
                new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                        .getResource("../images/black_horse.png"))).getImage());
        new Chariot(chessboard, blackPlayer, new Position(9, 10),
                new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                        .getResource("../images/black_chariot.png"))).getImage());
        new Chariot(chessboard, blackPlayer, new Position(1, 10),
                new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                        .getResource("../images/black_chariot.png"))).getImage());
        new Cannon(chessboard, blackPlayer, new Position(8, 8),
                new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                        .getResource("../images/black_cannon.png"))).getImage());
        new Cannon(chessboard, blackPlayer, new Position(2, 8),
                new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                        .getResource("../images/black_cannon.png"))).getImage());
        new Soldier(chessboard, blackPlayer, new Position(9, 7),
                new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                        .getResource("../images/black_soldier.png"))).getImage());
        new Soldier(chessboard, blackPlayer, new Position(7, 7),
                new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                        .getResource("../images/black_soldier.png"))).getImage());
        new Soldier(chessboard, blackPlayer, new Position(5, 7),
                new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                        .getResource("../images/black_soldier.png"))).getImage());
        new Soldier(chessboard, blackPlayer, new Position(3, 7),
                new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                        .getResource("../images/black_soldier.png"))).getImage());
        new Soldier(chessboard, blackPlayer, new Position(1, 7),
                new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                        .getResource("../images/black_soldier.png"))).getImage());
    }

    /**
     * 超级模式！在游戏开始时，把所有棋子加入棋盘
     */
    private void initializeChessboardEx() {
        new General(chessboard, redPlayer, new Position(5, 1),
                new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                        .getResource("../images/red_general.png"))).getImage());
        new Soldier(chessboard, redPlayer, new Position(1, 1),
                new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                        .getResource("../images/red_soldier.png"))).getImage());
        new Soldier(chessboard, redPlayer, new Position(2, 1),
                new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                        .getResource("../images/red_soldier.png"))).getImage());
        new Soldier(chessboard, redPlayer, new Position(3, 1),
                new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                        .getResource("../images/red_soldier.png"))).getImage());
        new Soldier(chessboard, redPlayer, new Position(4, 1),
                new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                        .getResource("../images/red_soldier.png"))).getImage());
        new Soldier(chessboard, redPlayer, new Position(6, 1),
                new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                        .getResource("../images/red_soldier.png"))).getImage());
        new Soldier(chessboard, redPlayer, new Position(7, 1),
                new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                        .getResource("../images/red_soldier.png"))).getImage());
        new Soldier(chessboard, redPlayer, new Position(8, 1),
                new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                        .getResource("../images/red_soldier.png"))).getImage());
        new Soldier(chessboard, redPlayer, new Position(9, 1),
                new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                        .getResource("../images/red_soldier.png"))).getImage());
        new Soldier(chessboard, redPlayer, new Position(1, 2),
                new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                        .getResource("../images/red_soldier.png"))).getImage());
        new Soldier(chessboard, redPlayer, new Position(2, 2),
                new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                        .getResource("../images/red_soldier.png"))).getImage());
        new Soldier(chessboard, redPlayer, new Position(3, 2),
                new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                        .getResource("../images/red_soldier.png"))).getImage());
        new Soldier(chessboard, redPlayer, new Position(4, 2),
                new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                        .getResource("../images/red_soldier.png"))).getImage());
        new Soldier(chessboard, redPlayer, new Position(5, 2),
                new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                        .getResource("../images/red_soldier.png"))).getImage());
        new Soldier(chessboard, redPlayer, new Position(6, 2),
                new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                        .getResource("../images/red_soldier.png"))).getImage());
        new Soldier(chessboard, redPlayer, new Position(7, 2),
                new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                        .getResource("../images/red_soldier.png"))).getImage());
        new Soldier(chessboard, redPlayer, new Position(8, 2),
                new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                        .getResource("../images/red_soldier.png"))).getImage());
        new Soldier(chessboard, redPlayer, new Position(9, 2),
                new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                        .getResource("../images/red_soldier.png"))).getImage());
        new Soldier(chessboard, redPlayer, new Position(1, 3),
                new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                        .getResource("../images/red_soldier.png"))).getImage());
        new Soldier(chessboard, redPlayer, new Position(2, 3),
                new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                        .getResource("../images/red_soldier.png"))).getImage());
        new Soldier(chessboard, redPlayer, new Position(3, 3),
                new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                        .getResource("../images/red_soldier.png"))).getImage());
        new Soldier(chessboard, redPlayer, new Position(4, 3),
                new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                        .getResource("../images/red_soldier.png"))).getImage());
        new Soldier(chessboard, redPlayer, new Position(5, 3),
                new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                        .getResource("../images/red_soldier.png"))).getImage());
        new Soldier(chessboard, redPlayer, new Position(6, 3),
                new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                        .getResource("../images/red_soldier.png"))).getImage());
        new Soldier(chessboard, redPlayer, new Position(7, 3),
                new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                        .getResource("../images/red_soldier.png"))).getImage());
        new Soldier(chessboard, redPlayer, new Position(8, 3),
                new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                        .getResource("../images/red_soldier.png"))).getImage());
        new Soldier(chessboard, redPlayer, new Position(9, 3),
                new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                        .getResource("../images/red_soldier.png"))).getImage());
        new Soldier(chessboard, redPlayer, new Position(1, 4),
                new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                        .getResource("../images/red_soldier.png"))).getImage());
        new Soldier(chessboard, redPlayer, new Position(2, 4),
                new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                        .getResource("../images/red_soldier.png"))).getImage());
        new Soldier(chessboard, redPlayer, new Position(3, 4),
                new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                        .getResource("../images/red_soldier.png"))).getImage());
        new Soldier(chessboard, redPlayer, new Position(4, 4),
                new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                        .getResource("../images/red_soldier.png"))).getImage());
        new Soldier(chessboard, redPlayer, new Position(5, 4),
                new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                        .getResource("../images/red_soldier.png"))).getImage());
        new Soldier(chessboard, redPlayer, new Position(6, 4),
                new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                        .getResource("../images/red_soldier.png"))).getImage());
        new Soldier(chessboard, redPlayer, new Position(7, 4),
                new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                        .getResource("../images/red_soldier.png"))).getImage());
        new Soldier(chessboard, redPlayer, new Position(8, 4),
                new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                        .getResource("../images/red_soldier.png"))).getImage());
        new Soldier(chessboard, redPlayer, new Position(9, 4),
                new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                        .getResource("../images/red_soldier.png"))).getImage());
        new Soldier(chessboard, redPlayer, new Position(1, 5),
                new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                        .getResource("../images/red_soldier.png"))).getImage());
        new Soldier(chessboard, redPlayer, new Position(2, 5),
                new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                        .getResource("../images/red_soldier.png"))).getImage());
        new Soldier(chessboard, redPlayer, new Position(3, 5),
                new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                        .getResource("../images/red_soldier.png"))).getImage());
        new Soldier(chessboard, redPlayer, new Position(4, 5),
                new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                        .getResource("../images/red_soldier.png"))).getImage());
        new Soldier(chessboard, redPlayer, new Position(5, 5),
                new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                        .getResource("../images/red_soldier.png"))).getImage());
        new Soldier(chessboard, redPlayer, new Position(6, 5),
                new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                        .getResource("../images/red_soldier.png"))).getImage());
        new Soldier(chessboard, redPlayer, new Position(7, 5),
                new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                        .getResource("../images/red_soldier.png"))).getImage());
        new Soldier(chessboard, redPlayer, new Position(8, 5),
                new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                        .getResource("../images/red_soldier.png"))).getImage());
        new Soldier(chessboard, redPlayer, new Position(9, 5),
                new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                        .getResource("../images/red_soldier.png"))).getImage());
        new General(chessboard, blackPlayer, new Position(5, 10),
                new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                        .getResource("../images/black_general.png"))).getImage());
        new Soldier(chessboard, blackPlayer, new Position(9, 10),
                new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                        .getResource("../images/black_soldier.png"))).getImage());
        new Soldier(chessboard, blackPlayer, new Position(8, 10),
                new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                        .getResource("../images/black_soldier.png"))).getImage());
        new Soldier(chessboard, blackPlayer, new Position(7, 10),
                new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                        .getResource("../images/black_soldier.png"))).getImage());
        new Soldier(chessboard, blackPlayer, new Position(6, 10),
                new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                        .getResource("../images/black_soldier.png"))).getImage());
        new Soldier(chessboard, blackPlayer, new Position(4, 10),
                new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                        .getResource("../images/black_soldier.png"))).getImage());
        new Soldier(chessboard, blackPlayer, new Position(3, 10),
                new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                        .getResource("../images/black_soldier.png"))).getImage());
        new Soldier(chessboard, blackPlayer, new Position(2, 10),
                new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                        .getResource("../images/black_soldier.png"))).getImage());
        new Soldier(chessboard, blackPlayer, new Position(1, 10),
                new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                        .getResource("../images/black_soldier.png"))).getImage());
        new Soldier(chessboard, blackPlayer, new Position(9, 9),
                new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                        .getResource("../images/black_soldier.png"))).getImage());
        new Soldier(chessboard, blackPlayer, new Position(8, 9),
                new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                        .getResource("../images/black_soldier.png"))).getImage());
        new Soldier(chessboard, blackPlayer, new Position(7, 9),
                new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                        .getResource("../images/black_soldier.png"))).getImage());
        new Soldier(chessboard, blackPlayer, new Position(6, 9),
                new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                        .getResource("../images/black_soldier.png"))).getImage());
        new Soldier(chessboard, blackPlayer, new Position(5, 9),
                new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                        .getResource("../images/black_soldier.png"))).getImage());
        new Soldier(chessboard, blackPlayer, new Position(4, 9),
                new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                        .getResource("../images/black_soldier.png"))).getImage());
        new Soldier(chessboard, blackPlayer, new Position(3, 9),
                new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                        .getResource("../images/black_soldier.png"))).getImage());
        new Soldier(chessboard, blackPlayer, new Position(2, 9),
                new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                        .getResource("../images/black_soldier.png"))).getImage());
        new Soldier(chessboard, blackPlayer, new Position(1, 9),
                new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                        .getResource("../images/black_soldier.png"))).getImage());
        new Soldier(chessboard, blackPlayer, new Position(9, 8),
                new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                        .getResource("../images/black_soldier.png"))).getImage());
        new Soldier(chessboard, blackPlayer, new Position(8, 8),
                new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                        .getResource("../images/black_soldier.png"))).getImage());
        new Soldier(chessboard, blackPlayer, new Position(7, 8),
                new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                        .getResource("../images/black_soldier.png"))).getImage());
        new Soldier(chessboard, blackPlayer, new Position(6, 8),
                new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                        .getResource("../images/black_soldier.png"))).getImage());
        new Soldier(chessboard, blackPlayer, new Position(5, 8),
                new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                        .getResource("../images/black_soldier.png"))).getImage());
        new Soldier(chessboard, blackPlayer, new Position(4, 8),
                new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                        .getResource("../images/black_soldier.png"))).getImage());
        new Soldier(chessboard, blackPlayer, new Position(3, 8),
                new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                        .getResource("../images/black_soldier.png"))).getImage());
        new Soldier(chessboard, blackPlayer, new Position(2, 8),
                new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                        .getResource("../images/black_soldier.png"))).getImage());
        new Soldier(chessboard, blackPlayer, new Position(1, 8),
                new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                        .getResource("../images/black_soldier.png"))).getImage());
        new Soldier(chessboard, blackPlayer, new Position(9, 7),
                new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                        .getResource("../images/black_soldier.png"))).getImage());
        new Soldier(chessboard, blackPlayer, new Position(8, 7),
                new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                        .getResource("../images/black_soldier.png"))).getImage());
        new Soldier(chessboard, blackPlayer, new Position(7, 7),
                new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                        .getResource("../images/black_soldier.png"))).getImage());
        new Soldier(chessboard, blackPlayer, new Position(6, 7),
                new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                        .getResource("../images/black_soldier.png"))).getImage());
        new Soldier(chessboard, blackPlayer, new Position(5, 7),
                new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                        .getResource("../images/black_soldier.png"))).getImage());
        new Soldier(chessboard, blackPlayer, new Position(4, 7),
                new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                        .getResource("../images/black_soldier.png"))).getImage());
        new Soldier(chessboard, blackPlayer, new Position(3, 7),
                new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                        .getResource("../images/black_soldier.png"))).getImage());
        new Soldier(chessboard, blackPlayer, new Position(2, 7),
                new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                        .getResource("../images/black_soldier.png"))).getImage());
        new Soldier(chessboard, blackPlayer, new Position(1, 7),
                new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                        .getResource("../images/black_soldier.png"))).getImage());
        new Soldier(chessboard, blackPlayer, new Position(9, 6),
                new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                        .getResource("../images/black_soldier.png"))).getImage());
        new Soldier(chessboard, blackPlayer, new Position(8, 6),
                new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                        .getResource("../images/black_soldier.png"))).getImage());
        new Soldier(chessboard, blackPlayer, new Position(7, 6),
                new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                        .getResource("../images/black_soldier.png"))).getImage());
        new Soldier(chessboard, blackPlayer, new Position(6, 6),
                new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                        .getResource("../images/black_soldier.png"))).getImage());
        new Soldier(chessboard, blackPlayer, new Position(5, 6),
                new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                        .getResource("../images/black_soldier.png"))).getImage());
        new Soldier(chessboard, blackPlayer, new Position(4, 6),
                new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                        .getResource("../images/black_soldier.png"))).getImage());
        new Soldier(chessboard, blackPlayer, new Position(3, 6),
                new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                        .getResource("../images/black_soldier.png"))).getImage());
        new Soldier(chessboard, blackPlayer, new Position(2, 6),
                new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                        .getResource("../images/black_soldier.png"))).getImage());
        new Soldier(chessboard, blackPlayer, new Position(1, 6),
                new ImageIcon(Objects.requireNonNull(ChessboardPanel.class
                        .getResource("../images/black_soldier.png"))).getImage());
    }

    /**
     * 用来获取棋子在画布上绘制的尺寸
     * @return 当前棋子绘制在画布上的宽与高
     */
    private int getPieceSize() {
        return (int)(0.09 * getHeight());
    }

    public void restart() {
        chessboard = new Chessboard();
        redPlayer = new Player(true);
        blackPlayer = new Player(false);

        initializeChessboard();
        turnPlayer = redPlayer;

        status = new ArrayList<>();
        status.add(chessboard.clone());
        step = 0;
    }

    public void ex() {
        chessboard = new Chessboard();
        redPlayer = new Player(true);
        blackPlayer = new Player(false);

        initializeChessboardEx();
        turnPlayer = redPlayer;

        status = new ArrayList<>();
        status.add(chessboard.clone());
        step = 0;
    }

    public void undo() {
        if (step == 0) {
            JOptionPane.showMessageDialog(this, "已经是最初的状态了。");
            return;
        }
        chessboard = status.get(step - 1).clone();
        step = step - 1;

        // 上一回合是另一个人着棋
        if (turnPlayer == redPlayer) turnPlayer = blackPlayer;
        else if (turnPlayer == blackPlayer) turnPlayer = redPlayer;
    }

    public void undoN(int n) {
        if (n < 0) {
            JOptionPane.showMessageDialog(this, "请勿输入负数。");
            return;
        }
        if (n > step) {
            n = step;
            JOptionPane.showMessageDialog(this, "您输入的步数超过了最大值，棋盘的状态将到达最初。");
        }
        chessboard = status.get(step - n).clone();
        step = step - n;

        // 上奇数回合是另一个人着棋
        if (n % 2 == 1) {
            if (turnPlayer == redPlayer) turnPlayer = blackPlayer;
            else if (turnPlayer == blackPlayer) turnPlayer = redPlayer;
        }
    }

    public void redo() {
        if (step + 1 == status.size()) {
            JOptionPane.showMessageDialog(this, "已经是最末的状态了。");
            return;
        }
        chessboard = status.get(step + 1).clone();
        step = step + 1;

        // 下一回合是另一个人着棋
        if (turnPlayer == redPlayer) turnPlayer = blackPlayer;
        else if (turnPlayer == blackPlayer) turnPlayer = redPlayer;
    }

    public void redoN(int n) {
        if (n < 0) {
            JOptionPane.showMessageDialog(this, "请勿输入负数。");
            return;
        }
        if (n > status.size() - step - 1) {
            n = status.size() - step - 1;
            JOptionPane.showMessageDialog(this, "您输入的步数超过了最大值，棋盘的状态将到达最末。");
        }
        chessboard = status.get(step + n).clone();
        step = step + n;

        // 上奇数回合是另一个人着棋
        if (n % 2 == 1) {
            if (turnPlayer == redPlayer) turnPlayer = blackPlayer;
            else if (turnPlayer == blackPlayer) turnPlayer = redPlayer;
        }
    }

    public ArrayList<Chessboard> getStatus() {
        return status;
    }

    public int getStep() {
        return step;
    }
}
