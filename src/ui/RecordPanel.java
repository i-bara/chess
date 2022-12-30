package ui;

import logic.Chessboard;
import logic.Position;
import logic.piece.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.nio.channels.UnresolvedAddressException;
import java.util.ArrayList;

/**
 * 棋谱 panel。
 * @version 2022.11.30 19:57
 */
public class RecordPanel extends JPanel {

    private static final URL iconURL = ChessboardPanel.class.getResource("../images/chessboard.png");
    private final ImageIcon icon;
    private final Image image;

    // 记忆过去的步骤
    private final ArrayList<Chessboard> status;
    private int step;

    RecordPanel(LayoutManager layoutManager, ArrayList<Chessboard> status, int step) {
        super(layoutManager);



        // 获取棋盘图像
        if (iconURL == null) throw new UnresolvedAddressException();
        icon = new ImageIcon(iconURL);
        image = icon.getImage();



        // 获取棋盘状态信息
        this.status = status;
        this.step = step;



        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                if (e.getButton() == 1) {
                    if (RecordPanel.this.step != 0) {
                        RecordPanel.this.step--;
                        repaint();
                    }
                }
                else if (e.getButton() == 3) {
                    if (RecordPanel.this.step + 1 != RecordPanel.this.status.size()) {
                        RecordPanel.this.step++;
                        repaint();
                    }
                }
            }
        });


    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Image scaledImage = image.getScaledInstance(-1, getHeight(), Image.SCALE_AREA_AVERAGING);
        icon.setImage(scaledImage);
        icon.paintIcon(this, g, 0, 0);

        for (Piece piece: status.get(step).getPieces()) {
            Image scaled = piece.getImage().getScaledInstance(-1, getPieceSize(),
                    Image.SCALE_AREA_AVERAGING);
            icon.setImage(scaled);
            icon.paintIcon(this, g, getPoint(piece.getPosition()).x, getPoint(piece.getPosition()).y);
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
     * 用来获取棋子在画布上绘制的尺寸
     * @return 当前棋子绘制在画布上的宽与高
     */
    private int getPieceSize() {
        return (int)(0.09 * getHeight());
    }
}
