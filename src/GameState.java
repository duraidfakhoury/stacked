import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class GameState {
    private List<ColoredPiece> pieces;
    private int[][] barriers;
    private int width, height;
    private GameState initialState;

    public GameState(int width, int height) {
        this.width = width;
        this.height = height;
        this.pieces = new ArrayList<>();
        this.barriers = new int[height][width];
    }

    public List<ColoredPiece> getPieces() {
        return pieces;
    }

    public int[][] getBarriers() {
        return barriers;
    }

    public void saveInitialState() {
        this.initialState = copy();
    }

    public void reset() {
        if (initialState != null) {
            this.pieces = new ArrayList<>();
            for (ColoredPiece piece : initialState.pieces) {
                this.pieces.add(new ColoredPiece(piece.getColor(), piece.getX(), piece.getY()));
            }
            for (int i = 0; i < height; i++) {
                System.arraycopy(initialState.barriers[i], 0, this.barriers[i], 0, width);
            }
        }
    }

    public GameState copy() {
        GameState newState = new GameState(width, height);
        for (ColoredPiece piece : pieces) {
            newState.pieces.add(new ColoredPiece(piece.getColor(), piece.getX(), piece.getY()));
        }
        for (int i = 0; i < height; i++) {
            System.arraycopy(barriers[i], 0, newState.barriers[i], 0, width);
        }
        return newState;
    }

    public boolean canMove(ColoredPiece piece, int dx, int dy) {
        int newX = piece.getX() + dx;
        int newY = piece.getY() + dy;

        if (newX < 0 || newX >= width || newY < 0 || newY >= height) {
            return false;
        }

        if (barriers[newY][newX] == 1) {
            return false;
        }

        for (ColoredPiece other : pieces) {
            if (other != piece && other.getX() == newX && other.getY() == newY) {
                if (other.getColor() == piece.getColor()) {
                    return true;
                } else {
                    return false;
                }
            }
        }

        return true;
    }

    public void movePieces(int dx, int dy) {
        List<ColoredPiece> movablePieces = new ArrayList<>();
        for (ColoredPiece piece : pieces) {
            if (canMove(piece, dx, dy)) {
                movablePieces.add(piece);
            }
        }

        for (ColoredPiece piece : movablePieces) {
            piece.setX(piece.getX() + dx);
            piece.setY(piece.getY() + dy);
        }
        if (movablePieces.isEmpty())
            return;
        else
            movePieces(dx, dy);
        mergePieces();
    }

    private void mergePieces() {
        boolean merged;
        do {
            merged = false;
            for (int i = 0; i < pieces.size(); i++) {
                for (int j = i + 1; j < pieces.size(); j++) {
                    ColoredPiece p1 = pieces.get(i);
                    ColoredPiece p2 = pieces.get(j);
                    if (p1.getColor() == p2.getColor() &&
                            p1.getX() == p2.getX() &&
                            p1.getY() == p2.getY()) {
                        pieces.remove(j);
                        merged = true;
                        break;
                    }
                }
                if (merged) break;
            }
        } while (merged);
    }

    public boolean isGameComplete() {
        Map<Integer, Integer> colorCount = new HashMap<>();
        for (ColoredPiece piece : pieces) {
            Integer color = Integer.valueOf(piece.getColor());
            colorCount.put(color, Integer.valueOf(colorCount.getOrDefault(color, Integer.valueOf(0)) + 1));
        }

        for (Integer count : colorCount.values()) {
            if (count > 1) return false;
        }
        return true;
    }
}