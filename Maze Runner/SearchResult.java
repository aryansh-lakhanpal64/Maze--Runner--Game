import java.util.List;

class SearchResult {
    final String algorithmName;
    final boolean pathFound;
    final List<int[]> visitOrder;
    final List<int[]> path;
    final int visitedNodes;
    final int peakFrontier;
    final int totalCostOrSteps;
    long executionTimeNanos;

    SearchResult(String algorithmName, boolean pathFound, List<int[]> visitOrder, List<int[]> path,
                 int visitedNodes, int peakFrontier, int totalCostOrSteps) {
        this.algorithmName = algorithmName;
        this.pathFound = pathFound;
        this.visitOrder = visitOrder;
        this.path = path;
        this.visitedNodes = visitedNodes;
        this.peakFrontier = peakFrontier;
        this.totalCostOrSteps = totalCostOrSteps;
        this.executionTimeNanos = 0L;
    }

    double getExecutionTimeMillis() {
        return executionTimeNanos / 1_000_000.0;
    }
}
