import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            BoardDesignFrame design = new BoardDesignFrame();
            design.setVisible(true);
        });
    }
}


