package de.lystx.cloudsystem.library.service.util;


public class Action {

    private final Long before;

    public Action() {
        this.before = System.currentTimeMillis();
    }

    public String getMS() {
        Long after = System.currentTimeMillis();
        long time = after - this.before;
        return ((int)(time / 1000L) % 60) + "." + (time / 100L);
    }

}
