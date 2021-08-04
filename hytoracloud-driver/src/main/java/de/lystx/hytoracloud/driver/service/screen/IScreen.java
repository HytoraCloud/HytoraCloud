package de.lystx.hytoracloud.driver.service.screen;

import de.lystx.hytoracloud.driver.command.executor.ConsoleExecutor;
import de.lystx.hytoracloud.driver.service.IService;

import java.io.File;
import java.util.List;

public interface IScreen {

    /**
     * The {@link IService} of this screen
     */
    IService getService();

    /**
     * The {@link Thread} of this screen
     */
    Thread getThread();

    /**
     * The {@link Process} of this screen
     */
    Process getProcess();

    /**
     * The directory of the process
     */
    File getDirectory();

    /**
     * If the screen is still running
     */
    boolean isRunning();

    /**
     * All cached lines of this screen
     */
    List<String> getCachedLines();

    /**
     * Sets the values for this screen to be able
     * to output the console to the printer
     *
     * @param console the console
     */
    void setPrinter(ConsoleExecutor console);

    /**
     * Sets the cached lines
     *
     * @param cachedLines the lines
     */
    void setCachedLines(List<String> cachedLines);

    /**
     * Starts the screen output
     */
    void start();

    /**
     * Stops the screen output
     */
    void stop();
}
