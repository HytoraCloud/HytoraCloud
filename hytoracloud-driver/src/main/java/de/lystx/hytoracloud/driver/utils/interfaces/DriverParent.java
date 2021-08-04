package de.lystx.hytoracloud.driver.utils.interfaces;

import de.lystx.hytoracloud.driver.command.executor.ConsoleExecutor;

public interface DriverParent {

    /**
     * Returns the Console
     *
     * @return console
     */
    ConsoleExecutor getConsole();

    /**
     * Reloads the instance
     */
    void reload();

    /**
     * Stops the instance
     */
    void shutdown();
}
