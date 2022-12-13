package ui;

import logic.Chessboard;
import logic.Player;
import logic.Position;
import logic.piece.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.UnresolvedAddressException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * 棋盘 panel。
 *
 * @version 2022.11.30 19:57
 */
public class ChessboardPanel extends JPanel {

    private static final URL iconURL = ChessboardPanel.class.getResource("../images/chessboard.png");
    private final ImageIcon icon;
    private final Image image;

    private HashSet<Piece> pieces;

    private Chessboard chessboard;
    private Player redPlayer;
    private Player blackPlayer;

    private Player turnPlayer;

    private Position focusPosition;
    private Piece focusPiece;
    private Position focusPositionToMove;

    private int focusStatus;

    private static final int NO_FOCUS = 0; // 尚未按下鼠标
    private static final int TO_FOCUS = 1; // 按下鼠标，但尚未确认是否这个棋子
    private static final int FOCUSED = 2; // 松开鼠标，棋子被拾起
    private static final int TO_MOVE = 3; // 按下鼠标，但尚未确认是否要移动

    ChessboardPanel(LayoutManager layoutManager) {
        super(layoutManager);
        pieces = new HashSet<Piece>();
        if (iconURL != null) {
            icon = new ImageIcon(iconURL);
            image = icon.getImage();
            System.out.println(image);
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
                if (position != null) {
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
                            if (focusPiece.canGoTo(position)) {
                                focusPiece.goTo(position);
                                pieces.remove(piece);
                                BufferedImage image = null;
                                try {
                                    image = ImageIO.read(iconURL);
                                } catch (IOException ex) {
                                    throw new RuntimeException(ex);
                                }
                                Image scaledImage = image.getScaledInstance(-1, 10, Image.SCALE_AREA_AVERAGING);
                                //Image croppedImage = scaledImage.

                                //g.drawImage(scaledImage, 0, 0, this);
                                // 绘制图片
                                icon.setImage(scaledImage);
                                icon.paintIcon(ChessboardPanel.this, getGraphics(), -100, -100);
                                //Image scaled = focusPiece.getImage().getScaledInstance(-1, (int)(0.09 * getHeight()), Image.SCALE_AREA_AVERAGING);
                                //icon.setImage(scaled);
                                //icon.paintIcon(ChessboardPanel.this, getGraphics(), getPoint(position).x, getPoint(position).y);
                                //paintComponent(getGraphics());
                                if (turnPlayer == redPlayer) turnPlayer = blackPlayer;
                                else if (turnPlayer == blackPlayer) turnPlayer = redPlayer;
                                focusPosition = null;
                                focusPiece = null;
                                focusPositionToMove = null;
                                focusStatus = NO_FOCUS;
                            }
                            else if (piece == null) {
                                focusPosition = null;
                                focusPiece = null;
                                focusStatus = NO_FOCUS;
                            }
                            else if (focusPiece.getPlayer() == piece.getPlayer()) {
                                focusPosition = position;
                                focusPiece = piece;
                                focusStatus = FOCUSED;
                            }
                        }
                        else {
                            focusPosition = null;
                            focusPiece = null;
                            focusStatus = NO_FOCUS;
                        }
                    }
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();
                String s = "当前鼠标坐标:(" + x + ", " + y + ")";
                //MouseMove.lab.setText(s);
            }
        });

    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        //image.

        // 缩放图片以适应高度
        Image scaledImage = image.getScaledInstance(-1, getHeight(), Image.SCALE_AREA_AVERAGING);

        //g.drawImage(scaledImage, 0, 0, this);
        // 绘制图片
        icon.setImage(scaledImage);
        icon.paintIcon(this, g, 0, 0);
        for (Piece piece: pieces) {
            Image scaled = piece.getImage().getScaledInstance(-1, (int)(0.09 * getHeight()), Image.SCALE_AREA_AVERAGING);
            icon.setImage(scaled);
            icon.paintIcon(this, g, getPoint(piece.getPosition()).x, getPoint(piece.getPosition()).y);
            // new ImageIcon(Objects.requireNonNull(ChessboardPanel.class.getResource("../images/icon.png"))).getImage()
        }

    }

    public ChessboardPanel addPiece(Piece piece) {
        pieces.add(piece);
        return this;
    }

    public Point getPoint(Position position) {
        return new Point((int)(0.027 * getHeight() + position.getX() * 0.0777 * getHeight()),
                (int)(0.88 * getHeight() - position.getY() * 0.0777 * getHeight()));
    }

    private Position getPosition(Point point) {
        int x = (int)((((float)point.x / getHeight()) - 0.027) / 0.0777);
        int y = (int)((0.88 - ((float)point.y / getHeight())) / 0.0777) + 1;
        Position position = new Position(x, y);
        Point point1 = new Point(getPoint(position).x + (int)(0.045 * getHeight()),
                getPoint(position).y + (int)(0.045 * getHeight()));
        if (point.distance(point1) <= 0.05 * getHeight()) return position;
        else return null;
    }

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

}
