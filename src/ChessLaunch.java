import ui.ChessUI;

public class ChessLaunch {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                //JFrame.setDefaultLookAndFeelDecorated(true);

                // 创建及设置窗口
                ChessUI frame = new ChessUI("chess");

            }
        });
    }
}
