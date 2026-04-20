import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

class Dijkstra {

    private static class Node {
        int r, c, dist;

        Node(int r, int c, int dist) {
            this.r = r;
            this.c = c;
            this.dist = dist;
        }
    }

    static SearchResult solve(MazeModel maze) {
        final int INF = Integer.MAX_VALUE / 4;

        int[][] dist = new int[maze.rows][maze.cols];
        boolean[][] visited = new boolean[maze.rows][maze.cols];
        int[][] parentR = new int[maze.rows][maze.cols];
        int[][] parentC = new int[maze.rows][maze.cols];

        for (int i = 0; i < maze.rows; i++) {
            Arrays.fill(dist[i], INF);
            Arrays.fill(parentR[i], -1);
            Arrays.fill(parentC[i], -1);
        }

        PriorityQueue<Node> pq = new PriorityQueue<Node>(new Comparator<Node>() {
            @Override
            public int compare(Node a, Node b) {
                return Integer.compare(a.dist, b.dist);
            }
        });

        List<int[]> visitOrder = new ArrayList<int[]>();

        dist[maze.startRow][maze.startCol] = 0;
        pq.offer(new Node(maze.startRow, maze.startCol, 0));

        int visitedCount = 0;
        int peakFrontier = 1;
        boolean found = false;

        int[][] dirs = {
            {-1, 0},
            {0, 1},
            {1, 0},
            {0, -1}
        };

        while (!pq.isEmpty()) {
            peakFrontier = Math.max(peakFrontier, pq.size());

            Node current = pq.poll();
            if (visited[current.r][current.c]) continue;

            visited[current.r][current.c] = true;
            visitOrder.add(new int[]{current.r, current.c});
            visitedCount++;

            if (current.r == maze.endRow && current.c == maze.endCol) {
                found = true;
                break;
            }

            for (int[] d : dirs) {
                int nr = current.r + d[0];
                int nc = current.c + d[1];

                if (!maze.isWalkable(nr, nc)) continue;

                int newDist = dist[current.r][current.c] + maze.moveCost(nr, nc);

                if (newDist < dist[nr][nc]) {
                    dist[nr][nc] = newDist;
                    parentR[nr][nc] = current.r;
                    parentC[nr][nc] = current.c;
                    pq.offer(new Node(nr, nc, newDist));
                }
            }
        }

        List<int[]> path = reconstructPath(maze, parentR, parentC, found);
        int totalCost = found ? dist[maze.endRow][maze.endCol] : -1;

        return new SearchResult("Dijkstra", found, visitOrder, path, visitedCount, peakFrontier, totalCost);
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
