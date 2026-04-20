import javax.swing.Timer;
import java.util.HashSet;
import java.util.Set;

class AnimationController {
    private final Set<String> visitedPaint = new HashSet<String>();
    private final Set<String> pathPaint = new HashSet<String>();

    private SearchResult result;
    private int visitIndex = 0;
    private int pathIndex = 0;
    private int currentRow = -1;
    private int currentCol = -1;
    private boolean animating = false;

    private Timer timer;
    private Runnable repaintCallback;
    private Runnable finishCallback;
    private int delayMs = 120;

    void reset() {
        visitedPaint.clear();
        pathPaint.clear();
        result = null;
        visitIndex = 0;
        pathIndex = 0;
        currentRow = -1;
        currentCol = -1;
        animating = false;

        if (timer != null && timer.isRunning()) {
            timer.stop();
        }
    }

    void play(SearchResult result, int delayMs, Runnable repaintCallback, Runnable finishCallback) {
        reset();
        this.result = result;
        this.repaintCallback = repaintCallback;
        this.finishCallback = finishCallback;
        this.delayMs = delayMs;
        this.animating = true;

        timer = new Timer(this.delayMs, e -> step());
        timer.start();
    }

    private void step() {
        if (result == null) {
            stop();
            return;
        }

        if (visitIndex < result.visitOrder.size()) {
            int[] cell = result.visitOrder.get(visitIndex);
            visitedPaint.add(key(cell[0], cell[1]));
            currentRow = cell[0];
            currentCol = cell[1];
            visitIndex++;
        } else if (result.pathFound && pathIndex < result.path.size()) {
            int[] cell = result.path.get(pathIndex);
            pathPaint.add(key(cell[0], cell[1]));
            currentRow = cell[0];
            currentCol = cell[1];
            pathIndex++;
        } else {
            stop();
            return;
        }

        if (repaintCallback != null) {
            repaintCallback.run();
        }
    }

    private void stop() {
        animating = false;
        if (timer != null) timer.stop();
        if (finishCallback != null) finishCallback.run();
    }

    private String key(int r, int c) {
        return r + "," + c;
    }

    boolean isVisited(int r, int c) {
        return visitedPaint.contains(key(r, c));
    }

    boolean isPath(int r, int c) {
        return pathPaint.contains(key(r, c));
    }

    boolean isCurrent(int r, int c) {
        return r == currentRow && c == currentCol;
    }

    boolean isAnimating() {
        return animating;
    }

    void setDelay(int delayMs) {
        this.delayMs = delayMs;
        if (timer != null) {
            timer.setDelay(delayMs);
            timer.setInitialDelay(delayMs);
        }
    }

    SearchResult getResult() {
        return result;
    }
}
