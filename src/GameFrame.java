import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;


class GameFrame extends JFrame {

    private GamePanel gamePanel;

    public GameFrame(GameState gameState) {
        super("Stacked Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        gameState.saveInitialState();


        gamePanel = new GamePanel(gameState);
        add(gamePanel);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int dx = 0, dy = 0;
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT:  dx = -1; break;
                    case KeyEvent.VK_RIGHT: dx = 1;  break;
                    case KeyEvent.VK_UP:    dy = -1; break;
                    case KeyEvent.VK_DOWN:  dy = 1;  break;
                    case KeyEvent.VK_R:
                        gameState.reset();
                        gamePanel.repaint();
                        return;
                }

                if (dx != 0 || dy != 0) {
                    gameState.movePieces(dx, dy);
                    gamePanel.repaint();

                    if (gameState.isGameComplete()) {
                        JOptionPane.showMessageDialog(
                                GameFrame.this,
                                "مبروك! لقد أكملت اللعبة!",
                                "تهانينا",
                                JOptionPane.INFORMATION_MESSAGE
                        );
                    }
                }
            }
        });

        JLabel instructions = new JLabel(
                "استخدم مفاتيح الأسهم للتحريك. اضغط R لإعادة اللعبة",
                SwingConstants.CENTER
        );
        add(instructions, BorderLayout.SOUTH);

        setFocusable(true);
        pack();
        setLocationRelativeTo(null);
    }
}
