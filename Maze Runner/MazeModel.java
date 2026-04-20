import java.util.Random;

class MazeModel {
    static final int EMPTY = 0;
    static final int WALL = 1;
    static final int MUD = 5;

    final int rows;
    final int cols;
    final int[][] grid;
    String levelName;

    int startRow = -1, startCol = -1;
    int endRow = -1, endCol = -1;

    MazeModel(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.grid = new int[rows][cols];
    }

    static MazeModel generate(String level) {
        if (level == null) return generateEasy(1);

        String lower = level.trim().toLowerCase();
        int tier = extractTier(lower);

        if (lower.startsWith("easy")) {
            return generateEasy(tier);
        } else if (lower.startsWith("medium")) {
            return generateMedium(tier);
        } else if (lower.startsWith("hard")) {
            return generateHard(tier);
        }

        return generateEasy(1);
    }

    private static int extractTier(String level) {
        // Expected format: "Easy 1", "Medium 3", "Hard 5"
        for (int i = level.length() - 1; i >= 0; i--) {
            if (Character.isDigit(level.charAt(i))) {
                int j = i;
                while (j >= 0 && Character.isDigit(level.charAt(j))) j--;
                try {
                    int value = Integer.parseInt(level.substring(j + 1, i + 1));
                    if (value >= 1 && value <= 5) return value;
                } catch (NumberFormatException ignored) {
                }
                break;
            }
        }
        return 1;
    }

    private static MazeModel generateEasy(int tier) {
        switch (tier) {
            case 2: return generateRandom(11, 11, 0.10, 0.02, "Easy 2");
            case 3: return generateRandom(12, 12, 0.12, 0.03, "Easy 3");
            case 4: return generateRandom(13, 13, 0.15, 0.04, "Easy 4");
            case 5: return generateRandom(14, 14, 0.18, 0.05, "Easy 5");
            case 1:
            default: return generateRandom(10, 10, 0.08, 0.02, "Easy 1");
        }
    }

    private static MazeModel generateMedium(int tier) {
        switch (tier) {
            case 2: return generateRandom(15, 15, 0.20, 0.05, "Medium 2");
            case 3: return generateRandom(16, 16, 0.23, 0.06, "Medium 3");
            case 4: return generateRandom(17, 17, 0.26, 0.07, "Medium 4");
            case 5: return generateRandom(18, 18, 0.29, 0.08, "Medium 5");
            case 1:
            default: return generateRandom(14, 14, 0.18, 0.04, "Medium 1");
        }
    }

    private static MazeModel generateHard(int tier) {
        switch (tier) {
            case 2: return generateRandom(19, 19, 0.28, 0.07, "Hard 2");
            case 3: return generateRandom(20, 20, 0.30, 0.09, "Hard 3");
            case 4: return generateRandom(21, 21, 0.32, 0.11, "Hard 4");
            case 5: return generateRandom(22, 22, 0.34, 0.12, "Hard 5");
            case 1:
            default: return generateRandom(18, 18, 0.26, 0.06, "Hard 1");
        }
    }

    private static MazeModel generateRandom(int rows, int cols, double wallProb, double mudProb, String name) {
        MazeModel maze = new MazeModel(rows, cols);
        maze.levelName = name;
        Random random = new Random();

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                // Guaranteed corridor: top row + last column are open.
                if (r == 0 || c == cols - 1) {
                    maze.grid[r][c] = EMPTY;
                } else {
                    double x = random.nextDouble();
                    if (x < wallProb) {
                        maze.grid[r][c] = WALL;
                    } else if (x < wallProb + mudProb) {
                        maze.grid[r][c] = MUD;
                    } else {
                        maze.grid[r][c] = EMPTY;
                    }
                }
            }
        }

        maze.startRow = 0;
        maze.startCol = 0;
        maze.endRow = rows - 1;
        maze.endCol = cols - 1;

        return maze;
    }

    boolean inBounds(int r, int c) {
        return r >= 0 && c >= 0 && r < rows && c < cols;
    }

    boolean isWalkable(int r, int c) {
        return inBounds(r, c) && grid[r][c] != WALL;
    }

    int moveCost(int r, int c) {
        if (grid[r][c] == MUD) return 5;
        return 1;
    }

    void setStart(int r, int c) {
        if (inBounds(r, c) && grid[r][c] != WALL) {
            startRow = r;
            startCol = c;
        }
    }

    void setEnd(int r, int c) {
        if (inBounds(r, c) && grid[r][c] != WALL) {
            endRow = r;
            endCol = c;
        }
    }

    void setWall(int r, int c) {
        if (inBounds(r, c) && !isStartOrEnd(r, c)) {
            grid[r][c] = WALL;
        }
    }

    void setMud(int r, int c) {
        if (inBounds(r, c) && !isStartOrEnd(r, c)) {
            grid[r][c] = MUD;
        }
    }

    void clearCell(int r, int c) {
        if (inBounds(r, c) && !isStartOrEnd(r, c)) {
            grid[r][c] = EMPTY;
        }
    }

    void toggleWall(int r, int c) {
        if (inBounds(r, c) && !isStartOrEnd(r, c)) {
            grid[r][c] = (grid[r][c] == WALL) ? EMPTY : WALL;
        }
    }

    boolean isStartOrEnd(int r, int c) {
        return (r == startRow && c == startCol) || (r == endRow && c == endCol);
    }
}
