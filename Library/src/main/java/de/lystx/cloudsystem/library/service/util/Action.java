package de.lystx.cloudsystem.library.service.util;


import lombok.Getter;
import lombok.Setter;

public class Action {

    private final Long before; //start time

    @Getter @Setter
    public String information;

    public Action() {
        this.before = System.currentTimeMillis();
    }

    /**
     * Gets the seconds
     * @return seconds this action took
     */
    public String getMS() {
        Long after = System.currentTimeMillis();
        long time = after - this.before;
        return ((int)(time / 1000L) % 60) + "." + (time / 100L);
    }

}
