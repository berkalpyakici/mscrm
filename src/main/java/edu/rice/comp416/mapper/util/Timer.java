package edu.rice.comp416.mapper.util;

/** Small timer utility to print out elapsed time. */
public class Timer {
    private long start;
    private long finish;

    /** Initialize and start the timer. */
    public Timer() {
        this.start = System.nanoTime();
        this.finish = this.start;
    }

    /** Stop the timer. */
    public void stop() {
        if (this.finish == this.start) {
            this.finish = System.nanoTime();
        }
    }

    /**
     * Get elapsed time in seconds. If the timer is still ticking, then it stops the timer before
     * returning elapsed time.
     *
     * @return Elapsed time in seconds.
     */
    public double getTimeInSeconds() {
        this.stop();
        return Math.round((this.finish - this.start) * 1e-9 * 100.0) / 100.0;
    }
}
