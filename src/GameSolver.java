import java.util.*;


class GameSolver {
    private static final int[] DX = {0, 0, -1, 1}; //  Up, Down, Left, Right
    private static final int[] DY = {-1, 1, 0, 0};
    private static final char[] MOVE_CHARS = {'U', 'D', 'L', 'R'};

    static class SearchStats {
        int visitedNodes = 0;
        int solutionNodes = 0;

        void reset() {
            visitedNodes = 0;
            solutionNodes = 0;
        }
    }

    static class GameNode {
        GameState state;
        List<Character> moves;

        GameNode(GameState state, List<Character> moves) {
            this.state = state;
            this.moves = moves;
        }
    }



    public record SearchResult(
            List<Character> solution,
            int visitedNodes,
            long executionTimeMillis
    ) {}

    public static SearchResult solveBFSWithStats(GameState initialState) {
        // Record start time
        long startTime = System.nanoTime();

        SearchStats stats = new SearchStats();
        Queue<GameNode> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();

        GameNode initialNode = new GameNode(initialState.copy(), new ArrayList<>());
        queue.offer(initialNode);
        visited.add(getStateHash(initialState));
        stats.visitedNodes++;

        while (!queue.isEmpty()) {
            GameNode current = queue.poll();

            if (current.state.isGameComplete()) {
                long endTime = System.nanoTime();
                long executionTime = (endTime - startTime) / 1_000_000; 
                System.out.println(executionTime);

                return new SearchResult(current.moves, stats.visitedNodes, executionTime);
            }

            for (int i = 3; i >= 0; i--) {
                GameState nextState = current.state.copy();
                nextState.movePieces(DX[i], DY[i]);

                String stateHash = getStateHash(nextState);
                if (!visited.contains(stateHash)) {
                    List<Character> newMoves = new ArrayList<>(current.moves);
                    newMoves.add(MOVE_CHARS[i]);

                    queue.offer(new GameNode(nextState, newMoves));
                    visited.add(stateHash);
                    stats.visitedNodes++;
                }
            }
        }

        long endTime = System.nanoTime();
        long executionTime = (endTime - startTime) / 1_000_000;

        return new SearchResult(null, stats.visitedNodes, executionTime);
    }

    public static SearchResult solveDFSWithStats(GameState initialState) {
        long startTime = System.nanoTime();

        SearchStats stats = new SearchStats();
        Stack<GameNode> stack = new Stack<>();
        Set<String> visited = new HashSet<>();

        GameNode initialNode = new GameNode(initialState.copy(), new ArrayList<>());
        stack.push(initialNode);
        visited.add(getStateHash(initialState));
        stats.visitedNodes++;

        while (!stack.isEmpty()) {
            GameNode current = stack.pop();

            if (current.state.isGameComplete()) {
                long endTime = System.nanoTime();
                long executionTime = (endTime - startTime) / 1_000_000;

                return new SearchResult(current.moves, stats.visitedNodes, executionTime);
            }

            for (int i = 3; i >= 0; i--) {
                GameState nextState = current.state.copy();
                nextState.movePieces(DX[i], DY[i]);

                String stateHash = getStateHash(nextState);
                if (!visited.contains(stateHash)) {
                    List<Character> newMoves = new ArrayList<>(current.moves);
                    newMoves.add(MOVE_CHARS[i]);

                    stack.push(new GameNode(nextState, newMoves));
                    visited.add(stateHash);
                    stats.visitedNodes++;
                }
            }
        }

        long endTime = System.nanoTime();
        long executionTime = (endTime - startTime) / 1_000_000;

        return new SearchResult(null, stats.visitedNodes, executionTime);
    }

    public static SearchResult solveRecursiveDFSWithStats(GameState initialState) {
        long startTime = System.nanoTime();

        SearchStats stats = new SearchStats();
        stats.reset();

        List<Character> solution = recursiveDFSHelperWithStats(
                initialState,
                new ArrayList<>(),
                new HashSet<>(),
                stats
        );

        long endTime = System.nanoTime();
        long executionTime = (endTime - startTime) / 1_000_000;

        return new SearchResult(solution, stats.visitedNodes, executionTime);
    }

    private static List<Character> recursiveDFSHelperWithStats(
            GameState currentState,
            List<Character> currentMoves,
            Set<String> visited,
            SearchStats stats
    ) {
        if (currentState.isGameComplete()) {
            return currentMoves;
        }

        String stateHash = getStateHash(currentState);

        if (visited.contains(stateHash)) {
            return null;
        }

        visited.add(stateHash);
        stats.visitedNodes++;

        for (int i = 0; i < 4; i++) {
            GameState nextState = currentState.copy();
            nextState.movePieces(DX[i], DY[i]);

            List<Character> newMoves = new ArrayList<>(currentMoves);
            newMoves.add(MOVE_CHARS[i]);

            List<Character> solution = recursiveDFSHelperWithStats(nextState, newMoves, visited, stats);

            if (solution != null) {
                return solution;
            }
        }

        return null;
    }

    private static String getStateHash(GameState state) {
        StringBuilder hash = new StringBuilder();
        List<ColoredPiece> pieces = state.getPieces();

        pieces.sort((p1, p2) -> {
            if (p1.getColor() != p2.getColor()) {
                return p1.getColor() - p2.getColor();
            }
            if (p1.getX() != p2.getX()) {
                return p1.getX() - p2.getX();
            }
            return p1.getY() - p2.getY();
        });

        for (ColoredPiece piece : pieces) {
            hash.append(piece.getColor())
                    .append(',')
                    .append(piece.getX())
                    .append(',')
                    .append(piece.getY())
                    .append(';');
        }

        return hash.toString();
    }

    // Existing getStateHash method remains the same...
}