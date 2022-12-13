package ui;

import logic.piece.Piece;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.nio.channels.UnresolvedAddressException;

public class PieceComponent extends JComponent {
    private URL iconURL;
    private final ImageIcon icon;
    private final Image image;

    private Piece piece;
    public PieceComponent(URL iconURL, Piece piece) {
        super();
        this.piece = piece;
        if (iconURL != null) {
            this.iconURL = iconURL;
            icon = new ImageIcon(iconURL);
            image = icon.getImage();
        }
        else throw new UnresolvedAddressException();
    }

    public Piece getPiece() {
        return piece;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // 缩放图片以适应高度
        Image scaledImage = image.getScaledInstance(-1, getHeight(), Image.SCALE_AREA_AVERAGING);

        // 绘制图片
        icon.setImage(scaledImage);
        icon.paintIcon(this, g, 0, 0);
    }
}
