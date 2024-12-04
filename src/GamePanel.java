import javax.swing.*;
import java.awt.*;
import java.util.List;

class GamePanel extends JPanel {
    private static final int CELL_SIZE = 60;
    private static final Color[] COLORS = {
            Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW,
            Color.ORANGE, Color.PINK, Color.CYAN, Color.MAGENTA
    };

    private GameState gameState;
    private JButton bfsSolveButton;
    private JButton dfsSolveButton;
    private JButton dfsRecursiveSolveButton;
    private JPanel solutionPanel;
    private JPanel controlPanel;
    private static final int MOVES_PER_ROW = 8;

    public GamePanel(GameState gameState) {
        this.gameState = gameState;
        setLayout(new BorderLayout());

        // Create the game board panel
        JPanel boardPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawGame((Graphics2D) g);
            }
        };
        boardPanel.setPreferredSize(new Dimension(
                CELL_SIZE * gameState.getBarriers()[0].length,
                CELL_SIZE * gameState.getBarriers().length
        ));

        // Create control panel with solve button and solution display
        createControlPanel();

        // Add components to the main panel
        add(boardPanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.EAST);
    }

    private JLabel statsLabel; // New label to display search statistics

    private void createControlPanel() {
        controlPanel = new JPanel();
        controlPanel.setLayout(new BorderLayout());
        controlPanel.setPreferredSize(new Dimension(300, getHeight()));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Create solve buttons
        bfsSolveButton = new JButton("BFS ");
        dfsSolveButton = new JButton("DFS ");
        dfsRecursiveSolveButton = new JButton("Recursive DFS ");

        bfsSolveButton.addActionListener(e -> findBfsSolution());
        dfsSolveButton.addActionListener(e -> findDfsSolution());
        dfsRecursiveSolveButton.addActionListener(e -> findDfsRecursiveSolution());

        // Create solution panel
        solutionPanel = new JPanel();
        solutionPanel.setBorder(BorderFactory.createTitledBorder("Solution Moves"));
        JScrollPane scrollPane = new JScrollPane(solutionPanel);
        scrollPane.setPreferredSize(new Dimension(280, 250)); // Reduced height to make room for stats

        // Create stats label
        statsLabel = new JLabel("Search Statistics");
        statsLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statsLabel.setFont(new Font("Arial", Font.BOLD, 12));

        // Add components to control panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(bfsSolveButton);
        buttonPanel.add(dfsSolveButton);
        buttonPanel.add(dfsRecursiveSolveButton);

        controlPanel.add(buttonPanel, BorderLayout.NORTH);
        controlPanel.add(scrollPane, BorderLayout.CENTER);
        controlPanel.add(statsLabel, BorderLayout.SOUTH);
    }

    private void findBfsSolution() {
        bfsSolveButton.setEnabled(false);
        dfsSolveButton.setEnabled(false);
        dfsRecursiveSolveButton.setEnabled(false);

        clearSolutionPanel();
        addLoadingMessage();
        resetStatsLabel();

        // Run the solver in a background thread
        SwingWorker<GameSolver.SearchResult, Void> worker = new SwingWorker<>() {
            @Override
            protected GameSolver.SearchResult doInBackground() {
                return GameSolver.solveBFSWithStats(gameState);
            }

            @Override
            protected void done() {
                try {
                    GameSolver.SearchResult result = get();
                    displaySolution(result.solution());
                    updateStatsLabel("BFS", result);
                } catch (Exception ex) {
                    showError("Error finding solution: " + ex.getMessage());
                }
                bfsSolveButton.setEnabled(true);
                dfsSolveButton.setEnabled(true);
                dfsRecursiveSolveButton.setEnabled(true);
            }
        };

        worker.execute();
    }

    private void findDfsSolution() {
        dfsSolveButton.setEnabled(false);
        bfsSolveButton.setEnabled(false);
        dfsRecursiveSolveButton.setEnabled(false);

        clearSolutionPanel();
        addLoadingMessage();
        resetStatsLabel();

        // Run the solver in a background thread
        SwingWorker<GameSolver.SearchResult, Void> worker = new SwingWorker<>() {
            @Override
            protected GameSolver.SearchResult doInBackground() {
                return GameSolver.solveDFSWithStats(gameState);
            }

            @Override
            protected void done() {
                try {
                    GameSolver.SearchResult result = get();
                    displaySolution(result.solution());
                    updateStatsLabel("DFS", result);
                } catch (Exception ex) {
                    showError("Error finding solution: " + ex.getMessage());
                }
                dfsSolveButton.setEnabled(true);
                bfsSolveButton.setEnabled(true);
                dfsRecursiveSolveButton.setEnabled(true);
            }
        };

        worker.execute();
    }

    private void findDfsRecursiveSolution() {
        dfsSolveButton.setEnabled(false);
        bfsSolveButton.setEnabled(false);
        dfsRecursiveSolveButton.setEnabled(false);

        clearSolutionPanel();
        addLoadingMessage();
        resetStatsLabel();

        // Run the solver in a background thread
        SwingWorker<GameSolver.SearchResult, Void> worker = new SwingWorker<>() {
            @Override
            protected GameSolver.SearchResult doInBackground() {
                return GameSolver.solveRecursiveDFSWithStats(gameState);
            }

            @Override
            protected void done() {
                try {
                    GameSolver.SearchResult result = get();
                    displaySolution(result.solution());
                    updateStatsLabel("R DFS", result);
                } catch (Exception ex) {
                    showError("Error finding solution: " + ex.getMessage());
                }
                dfsSolveButton.setEnabled(true);
                bfsSolveButton.setEnabled(true);
                dfsRecursiveSolveButton.setEnabled(true);
            }
        };

        worker.execute();
    }

    private void resetStatsLabel() {
        statsLabel.setText("Searching...");
        statsLabel.setForeground(Color.BLACK);
    }

    private void updateStatsLabel(String algorithmName, GameSolver.SearchResult result) {
        if (result.solution() == null) {
            statsLabel.setText("No solution found");
            statsLabel.setForeground(Color.RED);
        } else {
            String statsText = String.format(
                    "<html>%s:<br>" +
                            "Visited Nodes: %d<br>" +
                            "Solution Nodes: %d<br>" +
                            "Search Time: %d ms</html>",
                    algorithmName, result.visitedNodes(), result.solution().size(), result.executionTimeMillis()
            );
            statsLabel.setText(statsText);
            statsLabel.setForeground(Color.BLUE);
        }
    }

    private void clearSolutionPanel() {
        solutionPanel.removeAll();
        solutionPanel.revalidate();
        solutionPanel.repaint();
    }

    private void addLoadingMessage() {
        solutionPanel.setLayout(new BorderLayout());
        JLabel loadingLabel = new JLabel("Finding solution...", SwingConstants.CENTER);
        loadingLabel.setFont(new Font("Arial", Font.BOLD, 14));
        solutionPanel.add(loadingLabel, BorderLayout.CENTER);
    }

    private void displaySolution(List<Character> solution) {
        clearSolutionPanel();

        if (solution == null || solution.isEmpty()) {
            showError("No solution found!");
            return;
        }

        // Calculate grid layout dimensions
        int totalMoves = solution.size();
        int rows = (totalMoves + MOVES_PER_ROW - 1) / MOVES_PER_ROW;
        solutionPanel.setLayout(new GridLayout(rows, MOVES_PER_ROW, 5, 5));

        // Create move cells
        for (int i = 0; i < solution.size(); i++) {
            char move = solution.get(i);
            JPanel moveCell = createMoveCell(i + 1, move);
            solutionPanel.add(moveCell);
        }

        // Fill remaining grid cells if needed
        int remaining = (rows * MOVES_PER_ROW) - totalMoves;
        for (int i = 0; i < remaining; i++) {
            solutionPanel.add(new JPanel());
        }

        solutionPanel.revalidate();
        solutionPanel.repaint();
    }

    private JPanel createMoveCell(int moveNumber, char move) {
        JPanel cell = new JPanel();
        cell.setLayout(new BorderLayout());
        cell.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        cell.setBackground(Color.WHITE);

        // Convert move character to arrow
        String arrow = switch (move) {
            case 'U' -> "↑";
            case 'D' -> "↓";
            case 'L' -> "←";
            case 'R' -> "→";
            default -> String.valueOf(move);
        };

        // Create labels for move number and arrow
        JLabel numberLabel = new JLabel(String.valueOf(moveNumber));
        numberLabel.setHorizontalAlignment(SwingConstants.CENTER);
        numberLabel.setFont(new Font("Arial", Font.PLAIN, 10));

        JLabel arrowLabel = new JLabel(arrow);
        arrowLabel.setHorizontalAlignment(SwingConstants.CENTER);
        arrowLabel.setFont(new Font("Arial", Font.BOLD, 16));

        // Add labels to cell
        cell.add(numberLabel, BorderLayout.NORTH);
        cell.add(arrowLabel, BorderLayout.CENTER);

        return cell;
    }

    private void showError(String message) {
        clearSolutionPanel();
        solutionPanel.setLayout(new BorderLayout());
        JLabel errorLabel = new JLabel(message, SwingConstants.CENTER);
        errorLabel.setForeground(Color.RED);
        errorLabel.setFont(new Font("Arial", Font.BOLD, 14));
        solutionPanel.add(errorLabel, BorderLayout.CENTER);
    }

    private void drawGame(Graphics2D g2d) {
        // Draw white background
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        // Draw barriers
        g2d.setColor(Color.GRAY);
        int[][] barriers = gameState.getBarriers();
        for (int y = 0; y < barriers.length; y++) {
            for (int x = 0; x < barriers[y].length; x++) {
                if (barriers[y][x] == 1) {
                    g2d.fillRect(x * CELL_SIZE, y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                }
            }
        }

        // Draw grid
        g2d.setColor(Color.LIGHT_GRAY);
        for (int i = 0; i <= getWidth(); i += CELL_SIZE) {
            g2d.drawLine(i, 0, i, getHeight());
        }
        for (int i = 0; i <= getHeight(); i += CELL_SIZE) {
            g2d.drawLine(0, i, getWidth(), i);
        }

        // Draw pieces
        for (ColoredPiece piece : gameState.getPieces()) {
            g2d.setColor(COLORS[piece.getColor() % COLORS.length]);
            g2d.fillRect(
                    piece.getX() * CELL_SIZE + 5,
                    piece.getY() * CELL_SIZE + 5,
                    CELL_SIZE - 10,
                    CELL_SIZE - 10
            );
        }
    }
}