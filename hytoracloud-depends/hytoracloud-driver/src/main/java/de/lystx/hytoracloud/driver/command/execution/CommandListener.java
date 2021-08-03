package de.lystx.hytoracloud.driver.command.execution;

import de.lystx.hytoracloud.driver.command.executor.CommandExecutor;

/**
 * This class marks that a command is executed here
 */
public interface CommandListener {

    /**
     * Handles the executed command
     *
     * @param executor the executor
     * @param args the provided args
     */
    void execute(CommandExecutor executor, String[] args);

}
