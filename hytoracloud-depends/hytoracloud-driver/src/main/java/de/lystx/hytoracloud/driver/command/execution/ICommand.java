package de.lystx.hytoracloud.driver.command.execution;

import de.lystx.hytoracloud.driver.command.executor.CommandExecutor;

import java.io.Serializable;

public interface ICommand extends Serializable {

    /**
     * The name of this command
     */
    String getName();

    /**
     * The aliases of this command
     */
    String[] getAliases();

    /**
     * The description of this command
     */
    String getDescription();

    /**
     * Executes the command
     *
     * @param executor the executor
     * @param args the provided args
     */
    void execute(CommandExecutor executor, String[] args);
}
