import ui.ChessUI;

public class ChessLaunch {

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> new ChessUI("chess"));
    }
}
