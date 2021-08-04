package de.lystx.hytoracloud.driver.utils.other;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter @RequiredArgsConstructor
public class CloudRunnable {

    /**
     * The runnable
     */
    private final Runnable runnable;

    /**
     * If the runnable was executed
     */
    private boolean executed;

    /**
     * If the runnable should be executed
     * after the executed was set to true
     */
    @Setter
    private boolean stopAfterExecute;

    /**
     * All extra runnables
     */
    private final List<Runnable> runnables = new ArrayList<>();

    /**
     * Adds all runnables to the extra runnables
     *
     * @param runnable the runnables
     */
    public void add(Runnable... runnable) {
        this.runnables.addAll(Arrays.asList(runnable));
    }

    /**
     * Runs the runnable and marks it as executed
     */
    public void run() {
        for (Runnable rr : this.runnables) {
            rr.run();
        }
        if (this.executed && this.stopAfterExecute) {
            return;
        }
        this.executed = true;
        this.runnable.run();
    }

}
