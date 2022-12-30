package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.net.URL;
import java.nio.channels.UnresolvedAddressException;

/**
 * 顶层 GUI。
 *
 * @version 2022.11.30 19:57
 */

public class ChessUI extends JFrame implements KeyListener {

    private final ChessboardPanel chessboardPanel;

    private static final URL iconURL = ChessboardPanel.class.getResource("../images/icon.png");

    public ChessUI(String name) {
        super(name);

        // 设置默认关闭操作
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);



        // 图标
        if (iconURL == null) throw new UnresolvedAddressException();
        ImageIcon icon = new ImageIcon(iconURL);
        setIconImage(icon.getImage());



        // 主面板
        chessboardPanel = new ChessboardPanel(null);
        chessboardPanel.setPreferredSize(new Dimension(600,600));
        setContentPane(chessboardPanel);



        // 菜单栏
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(new Color(250, 250, 250));

        // 菜单
        JMenu gameMenu = new JMenu("游戏");
        JMenu aboutMenu = new JMenu("关于");
        menuBar.add(gameMenu);
        menuBar.add(aboutMenu);

        // “游戏”中的子菜单
        JMenuItem restartMenuItem = new JMenuItem("重开");
        JMenuItem undoMenuItem = new JMenuItem("撤回           Ctrl+Z");
        JMenuItem undoNMenuItem = new JMenuItem("撤回多步   Ctrl+Alt+Z");
        JMenuItem redoMenuItem = new JMenuItem("重做           Ctrl+Y");
        JMenuItem redoNMenuItem = new JMenuItem("重做多步   Ctrl+Alt+Y");
        JMenuItem recordMenuItem = new JMenuItem("棋谱");
        JMenuItem exitMenuItem = new JMenuItem("退出");
        restartMenuItem.addActionListener(actionEvent -> restart());
        undoMenuItem.addActionListener(actionEvent -> undo());
        undoNMenuItem.addActionListener(actionEvent -> undoN());
        redoMenuItem.addActionListener(actionEvent -> redo());
        redoNMenuItem.addActionListener(actionEvent -> redoN());
        recordMenuItem.addActionListener(actionEvent -> record());
        exitMenuItem.addActionListener(actionEvent -> exit());
        gameMenu.add(restartMenuItem);
        gameMenu.addSeparator();
        gameMenu.add(undoMenuItem);
        gameMenu.add(undoNMenuItem);
        gameMenu.add(redoMenuItem);
        gameMenu.add(redoNMenuItem);
        gameMenu.add(recordMenuItem);
        gameMenu.addSeparator();
        gameMenu.add(exitMenuItem);

        // “关于”中的子菜单
        JMenuItem aboutMenuItem = new JMenuItem("关于...");
        aboutMenuItem.addActionListener(actionEvent -> about());
        aboutMenu.add(aboutMenuItem);

        setJMenuBar(menuBar);



        // 键盘快捷键
        addKeyListener(this);



        // 打包并设置为可见
        pack();
        setVisible(true);

    }

    private void restart() {
        chessboardPanel.restart();
        chessboardPanel.repaint();
    }

    private void undo() {
        chessboardPanel.undo();
        chessboardPanel.repaint();
    }

    private void undoN() {
        String s = JOptionPane.showInputDialog("请输入要撤回的步数。");
        if (s != null) {
            int n = Integer.parseInt(s);
            chessboardPanel.undoN(n);
            chessboardPanel.repaint();
        }
    }

    private void redo() {
        chessboardPanel.redo();
        chessboardPanel.repaint();
    }

    private void redoN() {
        String s = JOptionPane.showInputDialog("请输入要重做的步数。");
        if (s != null) {
            int n = Integer.parseInt(s);
            chessboardPanel.redoN(n);
            chessboardPanel.repaint();
        }
    }

    private void record() {
        new RecordUI("棋谱：请按下左键以查看先前的棋盘，或按下右键以查看以后的棋盘", chessboardPanel);
    }

    private void exit() {
        System.exit(0);
    }

    private void about() {
        JOptionPane.showMessageDialog(chessboardPanel, "中国象棋",
                "关于...", JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void keyTyped(KeyEvent keyEvent) {

    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {
        if (keyEvent.isControlDown()) {
            if (keyEvent.isAltDown()) {
                if (keyEvent.getKeyCode() == 90) undoN();
                else if (keyEvent.getKeyCode() == 89) redoN();
            } else {
                if (keyEvent.getKeyCode() == 90) undo();
                else if (keyEvent.getKeyCode() == 89) redo();
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent keyEvent) {

    }
}
