package de.lystx.hytoracloud.driver.commons.minecraft.other;

import de.lystx.hytoracloud.driver.CloudDriver;

/**
 * This class gives you information
 * (TPS) Ticks per second about the cloud
 * It's like in Bukkit where 20* is the best
 * and the lower it gets the worse the performance is
 * It's not as precise as Bukkit so there might be like a small
 * difference between the real TPS and the displayed TPS
 */
public class TicksPerSecond {

    /**
     * The tick count (mostly 20)
     */
    public int tickCount;

    /**
     * The current ticks
     */
    public long[] ticks;

    /**
     * And the last tick
     */
    public long lastTick;

    public TicksPerSecond() {
        this.tickCount = 0;
        this.lastTick = 0L;
        this.ticks = new long[600];
        CloudDriver.getInstance().getScheduler().scheduleRepeatingTask(() -> {
            ticks[(tickCount% ticks.length)] = System.currentTimeMillis();
            tickCount+= 1;
        }, 100L, 1L);
    }

    /**
     * Gets TPS by 100 ticks
     *
     * @return the fps as double
     */
    public double getTPS() {
        return this.getTPS(100);
    }

    /**
     * Raw TPS Method
     *
     * @param tickss the ticks
     * @return TPS double
     */
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

    /**
     * Lag- O meter
     * @return Lag percentage
     */
    public double getLag() {
        return (double) Math.round((1.0D - this.getTPS() / 20.0D) * 100.0D);
    }

}
