import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Queue;

class BFS {

    static SearchResult solve(MazeModel maze) {
        boolean[][] visited = new boolean[maze.rows][maze.cols];
        int[][] parentR = new int[maze.rows][maze.cols];
        int[][] parentC = new int[maze.rows][maze.cols];

        for (int i = 0; i < maze.rows; i++) {
            Arrays.fill(parentR[i], -1);
            Arrays.fill(parentC[i], -1);
        }

        Queue<int[]> queue = new ArrayDeque<int[]>();
        List<int[]> visitOrder = new ArrayList<int[]>();

        queue.offer(new int[]{maze.startRow, maze.startCol});
        visited[maze.startRow][maze.startCol] = true;

        int visitedCount = 0;
        int peakFrontier = 1;
        boolean found = false;

        int[][] dirs = {
            {-1, 0},
            {0, 1},
            {1, 0},
            {0, -1}
        };

        while (!queue.isEmpty()) {
            peakFrontier = Math.max(peakFrontier, queue.size());

            int[] current = queue.poll();
            visitOrder.add(new int[]{current[0], current[1]});
            visitedCount++;

            int r = current[0];
            int c = current[1];

            if (r == maze.endRow && c == maze.endCol) {
                found = true;
                break;
            }

            for (int[] d : dirs) {
                int nr = r + d[0];
                int nc = c + d[1];

                if (maze.isWalkable(nr, nc) && !visited[nr][nc]) {
                    visited[nr][nc] = true;
                    parentR[nr][nc] = r;
                    parentC[nr][nc] = c;
                    queue.offer(new int[]{nr, nc});
                }
            }
        }

        List<int[]> path = reconstructPath(maze, parentR, parentC, found);
        int steps = path.isEmpty() ? -1 : path.size() - 1;

        return new SearchResult("BFS", found, visitOrder, path, visitedCount, peakFrontier, steps);
    }

    private static List<int[]> reconstructPath(MazeModel maze, int[][] parentR, int[][] parentC, boolean found) {
        List<int[]> path = new ArrayList<int[]>();
        if (!found) return path;

        int r = maze.endRow;
        int c = maze.endCol;

        while (r != -1 && c != -1) {
            path.add(new int[]{r, c});
            if (r == maze.startRow && c == maze.startCol) break;
            int pr = parentR[r][c];
            int pc = parentC[r][c];
            r = pr;
            c = pc;
        }

        Collections.reverse(path);
        return path;
    }
}
