package de.lystx.cloudsystem.other;

import de.lystx.cloudsystem.CloudSystem;
import de.lystx.cloudsystem.library.service.scheduler.Scheduler;

public class TicksPerSecond {

    public int tickCount;
    public long[] ticks;
    public long lastTick;


    public TicksPerSecond(CloudSystem cloudSystem) {
        this.tickCount = 0;
        this.lastTick = 0L;
        this.ticks= new long[600];
        cloudSystem.getService(Scheduler.class).scheduleRepeatingTask(() -> {
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
        int target = (tickCount- 1 - tickss) % ticks.length;
        long elapsed = System.currentTimeMillis() - ticks[target];

        return tickss / (elapsed / 1000.0D);
    }

    public double getLag() {
        return (double) Math.round((1.0D - this.getTPS() / 20.0D) * 100.0D);
    }

}
