package de.lystx.hytoracloud.driver.command.executor;

import de.lystx.hytoracloud.driver.setup.SetupExecutor;
import jline.console.ConsoleReader;
import jline.console.completer.Completer;

public interface ConsoleExecutor extends CommandExecutor {

    /**
     * Clears the screen of the console
     */
    void clearScreen();

    /**
     * Sets the current setup
     *
     * @param setup the setup
     */
    void setCurrentSetup(SetupExecutor<?> setup);

    /**
     * The prefix of console
     *
     * @return string prefix
     */
    String getPrefix();

    /**
     * The jline console reader
     *
     * @return reader instance
     */
    ConsoleReader getConsoleReader();

    /**
     * The command completer
     *
     * @return completer
     */
    Completer getCompleter();
}
