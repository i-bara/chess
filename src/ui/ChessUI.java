package ui;

import logic.Chessboard;
import logic.Player;
import logic.Position;
import logic.piece.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.net.URL;
import java.nio.channels.UnresolvedAddressException;
import java.util.Objects;

/**
 * 顶层GUI。
 *
 * @version 2022.11.30 19:57
 */

public class ChessUI extends JFrame {

    private static final URL iconURL = ChessboardPanel.class.getResource("../images/icon.png");

    private ChessboardPanel chessboardPanel;

    public ChessUI(String name) {
        super(name);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        if (iconURL != null) {
            ImageIcon icon = new ImageIcon(iconURL);
            Image image = icon.getImage();
            setIconImage(image);

            chessboardPanel = new ChessboardPanel(null);
            chessboardPanel.setPreferredSize(new Dimension(600,600));
//            chessboardPanel.addComponentListener(new ComponentAdapter() {
//                @Override
//                public void componentResized(ComponentEvent e) {
//                    super.componentResized(e);
//                    for (Component component: chessboardPanel.getComponents()) {
//                        if (component instanceof PieceComponent) {
//                            PieceComponent pieceComponent = (PieceComponent) component;
//                            component.setBounds(new Rectangle(chessboardPanel.getPoint(pieceComponent.getPiece().getPosition()),
//                                    new Dimension((int)(0.1 * chessboardPanel.getHeight()), (int)(0.1 * chessboardPanel.getHeight()))));
//                            //component.setBounds(-(int)(0.1 * chessboardPanel.getHeight()) / 2, -(int)(0.1 * chessboardPanel.getHeight()) / 2, (int)(0.1 * chessboardPanel.getHeight()), (int)(0.1 * chessboardPanel.getHeight()));
//                        }
//                        //component.setBounds((int)(0.1 * chessboardPanel.getHeight()), (int)(0.1 * chessboardPanel.getHeight()), (int)(0.1 * chessboardPanel.getHeight()), (int)(0.1 * chessboardPanel.getHeight()));
//                    }
//                }
//            });


            getContentPane().add(chessboardPanel);

            //PieceComponent pieceComponent = new PieceComponent(ChessboardPanel.class.getResource("../images/icon.png"), new Elephant(null, null, new Position(4,1)));

            //pieceComponent.setBounds(100, 100, 50, 50);
            //chessboardPanel.add(pieceComponent);

            pack();
            setVisible(true);
        }
        else throw new UnresolvedAddressException();

    }

    public ChessboardPanel getChessboardPanel() {
        return chessboardPanel;
    }
}
