import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

class MazePanel extends JPanel {
    private MazeModel maze;
    private final AnimationController animator = new AnimationController();

    private String editMode = "Wall";
    private String selectedAlgorithm = "BFS";
    private Runnable finishCallback;

    MazePanel(MazeModel maze) {
        this.maze = maze;
        setBackground(new Color(17, 24, 39));
        setPreferredSize(new Dimension(1000, 750));
        setMinimumSize(new Dimension(650, 500));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleClick(e);
            }
        });
    }

    void setOnSearchFinished(Runnable finishCallback) {
        this.finishCallback = finishCallback;
    }

    void setEditMode(String editMode) {
        this.editMode = editMode;
    }

    void setAlgorithm(String selectedAlgorithm) {
        this.selectedAlgorithm = selectedAlgorithm;
    }

    void setAnimationSpeed(int sliderValue) {
        // 1 -> slow, 10 -> fast
        int delay = 260 - (sliderValue * 20);
        if (delay < 30) delay = 30;
        animator.setDelay(delay);
    }

    void setMaze(MazeModel maze) {
        this.maze = maze;
        animator.reset();
        revalidate();
        repaint();
    }

    SearchResult getLastResult() {
        return animator.getResult();
    }

    boolean isAnimating() {
        return animator.isAnimating();
    }

    private void handleClick(MouseEvent e) {
        if (maze == null || animator.isAnimating()) return;

        GridMetrics metrics = calculateMetrics();
        if (!metrics.contains(e.getX(), e.getY())) return;

        int col = (e.getX() - metrics.offsetX) / metrics.cellSize;
        int row = (e.getY() - metrics.offsetY) / metrics.cellSize;

        if (!maze.inBounds(row, col)) return;

        if ("Set Start".equals(editMode)) {
            maze.setStart(row, col);
        } else if ("Set End".equals(editMode)) {
            maze.setEnd(row, col);
        } else if ("Wall".equals(editMode)) {
            maze.toggleWall(row, col);
        } else if ("Mud".equals(editMode)) {
            maze.setMud(row, col);
        } else if ("Erase".equals(editMode)) {
            maze.clearCell(row, col);
        }

        repaint();
    }

    void runSearch() {
        if (maze == null) return;

        if (maze.startRow < 0 || maze.endRow < 0) {
            JOptionPane.showMessageDialog(this, "Please set both Start and End points first.");
            return;
        }

        if (animator.isAnimating()) return;

        animator.reset();

        PerformanceMeter meter = new PerformanceMeter();
        meter.start();

        SearchResult result;
        if ("BFS".equalsIgnoreCase(selectedAlgorithm)) {
            result = BFS.solve(maze);
        } else if ("DFS".equalsIgnoreCase(selectedAlgorithm)) {
            result = DFS.solve(maze);
        } else {
            result = Dijkstra.solve(maze);
        }

        result.executionTimeNanos = meter.stop();

        animator.play(result, 120, this::repaint, () -> {
            repaint();
            if (finishCallback != null) finishCallback.run();
        });
    }

    void resetView() {
        animator.reset();
        repaint();
    }

    private GridMetrics calculateMetrics() {
        int padding = 20;
        int usableW = Math.max(1, getWidth() - padding * 2);
        int usableH = Math.max(1, getHeight() - padding * 2);
        int cell = Math.max(18, Math.min(usableW / maze.cols, usableH / maze.rows));
        int gridW = cell * maze.cols;
        int gridH = cell * maze.rows;
        int offsetX = (getWidth() - gridW) / 2;
        int offsetY = (getHeight() - gridH) / 2;
        return new GridMetrics(cell, offsetX, offsetY);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (maze == null) return;

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setFont(new Font("SansSerif", Font.BOLD, 12));

        GridMetrics metrics = calculateMetrics();

        // background board behind the grid
        int boardW = metrics.cellSize * maze.cols;
        int boardH = metrics.cellSize * maze.rows;
        g2.setColor(new Color(15, 23, 42));
        g2.fillRoundRect(metrics.offsetX - 8, metrics.offsetY - 8, boardW + 16, boardH + 16, 20, 20);

        for (int r = 0; r < maze.rows; r++) {
            for (int c = 0; c < maze.cols; c++) {
                int x = metrics.offsetX + c * metrics.cellSize;
                int y = metrics.offsetY + r * metrics.cellSize;

                Color color = new Color(255, 255, 255);
                if (maze.grid[r][c] == MazeModel.WALL) {
                    color = new Color(15, 15, 15);
                } else if (maze.grid[r][c] == MazeModel.MUD) {
                    color = new Color(127, 86, 48);
                } else {
                    color = new Color(241, 245, 249);
                }

                if (animator.isVisited(r, c)) {
                    color = new Color(147, 197, 253);
                }
                if (animator.isPath(r, c)) {
                    color = new Color(168, 85, 247);
                }
                if (r == maze.startRow && c == maze.startCol) {
                    color = new Color(34, 197, 94);
                }
                if (r == maze.endRow && c == maze.endCol) {
                    color = new Color(239, 68, 68);
                }

                g2.setColor(color);
                g2.fillRoundRect(x + 1, y + 1, metrics.cellSize - 2, metrics.cellSize - 2, 8, 8);

                // grid border
                g2.setColor(new Color(203, 213, 225, 120));
                g2.drawRoundRect(x + 1, y + 1, metrics.cellSize - 2, metrics.cellSize - 2, 8, 8);

                if (r == maze.startRow && c == maze.startCol) {
                    g2.setColor(Color.WHITE);
                    g2.drawString("S", x + metrics.cellSize / 2 - 4, y + metrics.cellSize / 2 + 4);
                } else if (r == maze.endRow && c == maze.endCol) {
                    g2.setColor(Color.WHITE);
                    g2.drawString("E", x + metrics.cellSize / 2 - 4, y + metrics.cellSize / 2 + 4);
                }
            }
        }

        // current ghost / active node
        for (int r = 0; r < maze.rows; r++) {
            for (int c = 0; c < maze.cols; c++) {
                if (animator.isCurrent(r, c)) {
                    int x = metrics.offsetX + c * metrics.cellSize;
                    int y = metrics.offsetY + r * metrics.cellSize;
                    g2.setColor(new Color(255, 255, 255, 60));
                    g2.fillOval(x + 4, y + 4, metrics.cellSize - 8, metrics.cellSize - 8);
                    g2.setColor(new Color(255, 165, 0));
                    g2.setStroke(new BasicStroke(2f));
                    g2.drawOval(x + 4, y + 4, metrics.cellSize - 8, metrics.cellSize - 8);
                }
            }
        }

        g2.dispose();
    }

    private static class GridMetrics {
        final int cellSize;
        final int offsetX;
        final int offsetY;

        GridMetrics(int cellSize, int offsetX, int offsetY) {
            this.cellSize = cellSize;
            this.offsetX = offsetX;
            this.offsetY = offsetY;
        }

        boolean contains(int x, int y) {
            return x >= offsetX && y >= offsetY;
        }
    }
}
