package de.lystx.hytoracloud.driver.utils;


import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * This class is used as a kind of
 * stopwatch to create an action
 * and stop ist and then see how long the
 * action took to process.
 */
public class Action implements Serializable {

    private static final long serialVersionUID = -2638152143987142961L;
    private final Long before; //start time

    @Getter @Setter
    public String information;

    public Action() {
        this.before = System.currentTimeMillis();
    }

    /**
     * Gets the seconds
     *
     * @return seconds this action took
     */
    public String getMS() {
        long time = this.time();
        return ((int)(time / 1000L) % 60) + "." + (time / 100L);
    }

    /**
     * The final end time
     * the action took to process
     *
     * @return long
     */
    public long time() {
        Long after = System.currentTimeMillis();
        return after - this.before;
    }

}
