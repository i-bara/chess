package ui;

import logic.Chessboard;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.nio.channels.UnresolvedAddressException;
import java.util.ArrayList;

public class RecordUI extends JFrame {

    private static final URL iconURL = ChessboardPanel.class.getResource("../images/icon.png");

    RecordUI (String name, ChessboardPanel chessboardPanel) {
        super(name);

        // 复制当前状态的信息
        ArrayList<Chessboard> status = new ArrayList<>();
        for (int i = 0; i < chessboardPanel.getStatus().size(); i++) {
            status.add(chessboardPanel.getStatus().get(i).clone());
        }
        int step = chessboardPanel.getStep();



        // 图标
        if (iconURL == null) throw new UnresolvedAddressException();
        ImageIcon icon = new ImageIcon(iconURL);
        setIconImage(icon.getImage());



        // 主面板
        RecordPanel recordPanel = new RecordPanel(null, status, step);
        recordPanel.setPreferredSize(new Dimension(600,600));
        setContentPane(recordPanel);



        // 打包并设置为可见
        pack();
        setVisible(true);
    }
}
