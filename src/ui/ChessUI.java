package ui;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.nio.channels.UnresolvedAddressException;

/**
 * 顶层GUI。
 *
 * @version 2022.11.30 19:57
 */

public class ChessUI extends JFrame {

    private static final URL iconURL = ChessboardPanel.class.getResource("../images/icon.png");

    public ChessUI(String name) {
        super(name);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        if (iconURL != null) {
            ImageIcon icon = new ImageIcon(iconURL);
            Image image = icon.getImage();
            setIconImage(image);

            ChessboardPanel chessboardPanel = new ChessboardPanel();
            chessboardPanel.setPreferredSize(new Dimension(600,600));
            getContentPane().add(chessboardPanel);

            pack();
            setVisible(true);
        }
        else throw new UnresolvedAddressException();

    }



}
