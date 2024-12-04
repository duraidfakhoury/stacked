import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

class BoardDesignFrame extends JFrame {
    private int width = 2;
    private int height = 2;
    private int numColors = 3;
    private JPanel designPanel;
    private ArrayList<ArrayList<Cell>> cells;
    private JComboBox<String> currentSelectionType;
    private JComboBox<Integer> colorSelector;

    public BoardDesignFrame() {
        super("تصميم الرقعة");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // إعدادات اللوحة الرئيسية
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        add(mainPanel);

        // لوحة التحكم
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        setupControlPanel(controlPanel);
        mainPanel.add(controlPanel, BorderLayout.NORTH);

        // لوحة الأدوات لاختيار نوع الخلية واللون
        JPanel toolPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        setupToolPanel(toolPanel);
        mainPanel.add(toolPanel, BorderLayout.CENTER);

        // إنشاء لوحة الشبكة
        createDesignPanel();
        JScrollPane scrollPane = new JScrollPane(designPanel);
        scrollPane.setPreferredSize(new Dimension(400, 300));
        mainPanel.add(scrollPane, BorderLayout.SOUTH);

        // زر بدء اللعبة
        JButton startButton = new JButton("بدء اللعبة");
        mainPanel.add(startButton, BorderLayout.EAST);

        // إجراء زر "بدء اللعبة"
        startButton.addActionListener(e -> startGame());

        pack();
        setLocationRelativeTo(null);
    }

    // إعداد لوحة التحكم
    private void setupControlPanel(JPanel controlPanel) {
        SpinnerModel widthModel = new SpinnerNumberModel(2, 2, 10, 1);
        SpinnerModel heightModel = new SpinnerNumberModel(2, 2, 10, 1);
        SpinnerModel colorsModel = new SpinnerNumberModel(3, 1, 8, 1);

        final JSpinner widthSpinner = new JSpinner(widthModel);
        final JSpinner heightSpinner = new JSpinner(heightModel);
        final JSpinner colorsSpinner = new JSpinner(colorsModel);

        controlPanel.add(new JLabel("العرض:"));
        controlPanel.add(widthSpinner);
        controlPanel.add(new JLabel("الارتفاع:"));
        controlPanel.add(heightSpinner);
        controlPanel.add(new JLabel("عدد الألوان:"));
        controlPanel.add(colorsSpinner);

        JButton applyButton = new JButton("تطبيق");
        controlPanel.add(applyButton);

        // إجراء زر "تطبيق" لتحديث الشبكة
        applyButton.addActionListener(e -> {
            width = (Integer) widthSpinner.getValue();
            height = (Integer) heightSpinner.getValue();
            numColors = (Integer) colorsSpinner.getValue();
            updateColorSelector();
            refreshDesignPanel();
        });
    }

    // إعداد لوحة الأدوات
    private void setupToolPanel(JPanel toolPanel) {
        String[] selectionTypes = {"فارغ", "حاجز", "قطعة ملونة"};
        currentSelectionType = new JComboBox<>(selectionTypes);
        colorSelector = new JComboBox<>();
        updateColorSelector();

        toolPanel.add(new JLabel("نوع الخلية:"));
        toolPanel.add(currentSelectionType);
        toolPanel.add(new JLabel("اللون:"));
        toolPanel.add(colorSelector);
    }

    // إنشاء لوحة التصميم (الشبكة)
    private void createDesignPanel() {
        designPanel = new JPanel(new GridLayout(height, width, 2, 2));
        designPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        cells = new ArrayList<>();
        for (int y = 0; y < height; y++) {
            ArrayList<Cell> row = new ArrayList<>();
            for (int x = 0; x < width; x++) {
                Cell cell = new Cell(x, y);
                row.add(cell);
                designPanel.add(cell);

                // إعداد المستمع لكل خلية
                cell.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        Cell clickedCell = (Cell) e.getSource();
                        String selection = (String) currentSelectionType.getSelectedItem();

                        if ("فارغ".equals(selection)) {
                            clickedCell.setType(CellType.EMPTY);
                        } else if ("حاجز".equals(selection)) {
                            clickedCell.setType(CellType.BARRIER);
                        } else if ("قطعة ملونة".equals(selection)) {
                            clickedCell.setType(CellType.COLORED);
                            clickedCell.setColorIndex((Integer) colorSelector.getSelectedItem());
                        }
                        clickedCell.repaint();
                    }
                });
            }
            cells.add(row);
        }
    }

    // تحديث لوحة التصميم بالكامل عند تغيير الإعدادات
    private void refreshDesignPanel() {
        designPanel.removeAll(); // إزالة جميع الخلايا الحالية من اللوحة
        designPanel.setLayout(new GridLayout(height, width, 2, 2)); // تحديث التخطيط الجديد

        cells.clear(); // إعادة تهيئة قائمة الخلايا
        for (int y = 0; y < height; y++) {
            ArrayList<Cell> row = new ArrayList<>();
            for (int x = 0; x < width; x++) {
                Cell cell = new Cell(x, y);
                row.add(cell);
                designPanel.add(cell);

                // إعداد المستمع لكل خلية
                cell.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        Cell clickedCell = (Cell) e.getSource();
                        String selection = (String) currentSelectionType.getSelectedItem();

                        if ("فارغ".equals(selection)) {
                            clickedCell.setType(CellType.EMPTY);
                        } else if ("حاجز".equals(selection)) {
                            clickedCell.setType(CellType.BARRIER);
                        } else if ("قطعة ملونة".equals(selection)) {
                            clickedCell.setType(CellType.COLORED);
                            clickedCell.setColorIndex((Integer) colorSelector.getSelectedItem());
                        }
                        clickedCell.repaint();
                    }
                });
            }
            cells.add(row);
        }

        designPanel.revalidate();
        designPanel.repaint();
        pack();
    }

    // تحديث قائمة الألوان المتاحة
    private void updateColorSelector() {
        colorSelector.removeAllItems();
        for (int i = 0; i < numColors; i++) {
            colorSelector.addItem(i);
        }
    }

    // بدء اللعبة
    private void startGame() {
        GameState gameState = createGameState();
        GameFrame gameFrame = new GameFrame(gameState);
        gameFrame.setVisible(true);
        dispose();
    }

    // إنشاء حالة اللعبة بناءً على الخلايا الحالية
    private GameState createGameState() {
        GameState gameState = new GameState(width,height);
        ArrayList<ColoredPiece> pieces = new ArrayList<>();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Cell cell = cells.get(y).get(x);
                if (cell.getType() == CellType.BARRIER) {
                    gameState.getBarriers()[y][x] = 1;
                } else if (cell.getType() == CellType.COLORED) {
                    pieces.add(new ColoredPiece(cell.getColorIndex(), x, y));
                }
            }
        }

        gameState.getPieces().addAll(pieces);
        gameState.saveInitialState();
        return gameState;
    }
}

enum CellType {
    EMPTY, BARRIER, COLORED
}

class Cell extends JPanel {
    private static final int CELL_SIZE = 60;
    private static final Color[] COLORS = {
            Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW,
            Color.ORANGE, Color.PINK, Color.CYAN, Color.MAGENTA
    };

    private CellType type = CellType.EMPTY;
    private int colorIndex = 0;

    public Cell(int x, int y) {
        setPreferredSize(new Dimension(CELL_SIZE, CELL_SIZE));
        setBorder(BorderFactory.createLineBorder(Color.GRAY));
    }

    public void setType(CellType type) {
        this.type = type;
    }

    public CellType getType() {
        return type;
    }

    public void setColorIndex(int colorIndex) {
        this.colorIndex = colorIndex;
    }

    public int getColorIndex() {
        return colorIndex;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        switch (type) {
            case EMPTY -> setBackground(Color.WHITE);
            case BARRIER -> setBackground(Color.GRAY);
            case COLORED -> setBackground(COLORS[colorIndex % COLORS.length]);
        }
    }
}
