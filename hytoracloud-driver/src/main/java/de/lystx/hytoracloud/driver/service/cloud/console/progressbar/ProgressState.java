package de.lystx.hytoracloud.driver.service.cloud.console.progressbar;

import java.time.Duration;
import java.time.Instant;


class ProgressState {

    String taskName;
    String extraMessage = "";

    boolean indefinite = false;

    long start;
    long current;
    long max;

    Instant startInstant = null;
    Duration elapsedBeforeStart = Duration.ZERO;

    volatile boolean alive = true;
    volatile boolean paused = false;

    ProgressState(String taskName, long initialMax, long startFrom, Duration elapsedBeforeStart) {
        this.taskName = taskName;
        this.max = initialMax;
        if (initialMax < 0) indefinite = true;
        this.start = startFrom;
        this.current = startFrom;
        this.elapsedBeforeStart = elapsedBeforeStart;
        this.startInstant = Instant.now();
    }

    String getTaskName() {
        return taskName;
    }

    synchronized String getExtraMessage() {
        return extraMessage;
    }

    synchronized long getCurrent() {
        return current;
    }

    synchronized long getMax() {
        return max;
    }

    synchronized double getNormalizedProgress() {
        if (max <= 0) return 0.0;
        else if (current > max) return 1.0;
        else return ((double)current) / max;
    }

    synchronized void setAsDefinite() {
        indefinite = false;
    }

    synchronized void setAsIndefinite() {
        indefinite = true;
    }

    synchronized void maxHint(long n) {
        max = n;
    }

    synchronized void stepBy(long n) {
        current += n;
        if (current > max) max = current;
    }

    synchronized void stepTo(long n) {
        current = n;
        if (current > max) max = current;
    }

    synchronized void setExtraMessage(String msg) {
        extraMessage = msg;
    }

    synchronized void pause() {
        paused = true;
        start = current;
        elapsedBeforeStart = elapsedBeforeStart.plus(Duration.between(startInstant, Instant.now()));
    }

    synchronized void resume() {
        paused = false;
        startInstant = Instant.now();
    }

    synchronized void kill() {
        alive = false;
    }

}
