package de.lystx.hytoracloud.driver.command;

import de.lystx.hytoracloud.driver.wrapped.CommandObject;
import de.lystx.hytoracloud.driver.command.execution.ICommand;
import de.lystx.hytoracloud.driver.command.executor.CommandExecutor;
import de.lystx.hytoracloud.driver.command.execution.CommandListener;

import java.util.List;

public interface ICommandManager {

    /**
     * Gets a list of all cached {@link CommandObject}s
     *
     * @return list of info
     */
    List<ICommand> getCommands();

    /**
     * Searches for a {@link CommandObject} by
     * name or alias it doesn't matter
     *
     * @param nameOrAlias the name or alias
     * @return info or null
     */
    ICommand getCommand(String nameOrAlias);

    /**
     * Gets a list of all registered {@link CommandListener}s
     *
     * @return list of listener
     */
    List<CommandListener> getListeners();

    /**
     * Searches for a {@link CommandListener} by
     * name or alias it doesn't matter
     *
     * @param commandOrAlias the name or alias
     * @return info or null
     */
    CommandListener getListener(String commandOrAlias);

    /**
     * Registers a command
     *
     * @param command the command
     */
    void registerCommand(CommandListener command);

    /**
     * Unregisters a command with a provided class
     * that was registered before
     *
     * @param command the class that was registered
     */
    void unregisterCommand(Class<? extends CommandListener> command);

    /**
     * Executes a command for an {@link CommandExecutor} with given {@link String[]} args
     *
     * @param executor the executor
     * @param commandLine the commandLine
     */
    void executeCommand(CommandExecutor executor, String commandLine);

    /**
     * Makes the cloud execute a command
     *
     * @param command the command line
     */
    void sendCommandToCloud(String command);

    /**
     * Executes a command for an {@link CommandExecutor} with given {@link String[]} args
     *
     * @param executor the executor
     * @param replacePrefix if the prefix ("/") should be replaced
     * @param commandLine the commandLine
     * @return if command exists
     */
    boolean executeCommand(CommandExecutor executor, boolean replacePrefix, String commandLine);


    /**
     * Toggles the manager to execute
     * commands
     *
     * @param b the boolean
     */
    void setActive(boolean b);

    /**
     * Checks if manager is active
     *
     * @return boolean
     */
    boolean isActive();
}
