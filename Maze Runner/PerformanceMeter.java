class PerformanceMeter {
    private long startTime;

    void start() {
        startTime = System.nanoTime();
    }

    long stop() {
        return System.nanoTime() - startTime;
    }
}
