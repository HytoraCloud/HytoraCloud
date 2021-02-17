package de.lystx.cloudsystem.library.service.util;

import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.service.scheduler.Scheduler;

public class TicksPerSecond {

    public int tickCount;
    public long[] ticks;
    public long lastTick;


    public TicksPerSecond(CloudLibrary cloudLibrary) {
        this.tickCount = 0;
        this.lastTick = 0L;
        this.ticks= new long[600];
        cloudLibrary.getService(Scheduler.class).scheduleRepeatingTask(() -> {
            ticks[(tickCount% ticks.length)] = System.currentTimeMillis();
            tickCount+= 1;
        }, 100L, 1L);
    }

    public double getTPS() {
        return this.getTPS(100);
    }

    public double getTPS(int tickss) {
        if (tickCount< tickss) {
            return 20.0D;
        }
        try {
            int target = (tickCount- 1 - tickss) % ticks.length;
            long elapsed = System.currentTimeMillis() - ticks[target];
            return tickss / (elapsed / 1000.0D);
        } catch (ArrayIndexOutOfBoundsException e) {
            return tickss;
        }

    }

    public double getLag() {
        return (double) Math.round((1.0D - this.getTPS() / 20.0D) * 100.0D);
    }

}
