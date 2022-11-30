package ui;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.nio.channels.UnresolvedAddressException;

/**
 * 棋盘 panel。
 *
 * @version 2022.11.30 19:57
 */
public class ChessboardPanel extends JPanel {

    private static final URL iconURL = ChessboardPanel.class.getResource("../images/chessboard.png");
    private final ImageIcon icon;
    private final Image image;

    ChessboardPanel() {
        super();
        if (iconURL != null) {
            System.out.println(iconURL);
            icon = new ImageIcon(iconURL);
            image = icon.getImage();
        }
        else throw new UnresolvedAddressException();

    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // 缩放图片以适应高度
        Image scaledImage = image.getScaledInstance(-1, getHeight(), Image.SCALE_AREA_AVERAGING);

        // 绘制图片
        icon.setImage(scaledImage);
        icon.paintIcon(this, g, 0, 0);
        System.out.println(g);
    }

}
