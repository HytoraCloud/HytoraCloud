package de.lystx.hytoracloud.driver.commons.interfaces;

import utillity.PropertyObject;

import java.lang.management.ManagementFactory;

public interface BridgeInstance {

    /**
     * Bootstraps this instance
     */
    void bootstrap();

    /**
     * Executes a console command
     *
     * @param command the command
     */
    void flushCommand(String command);

    /**
     * Responds with a list of important
     * information on this instance
     * that might be important or
     * useful to see
     *
     * @return object
     */
    PropertyObject requestProperties();

    /**
     * Loads the current memory usage
     * @return
     */
    default long loadMemoryUsage() {
        return ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed() / 1048576L;
    }

    /**
     * Loads the current TPS
     * (Ticks per second)
     *
     * @return tps formatted with color
     */
    default String loadTPS() {
        return "§c???";
    }

    /**
     * Stops this instance
     */
    void shutdown();
}
