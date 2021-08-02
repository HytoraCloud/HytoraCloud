package de.lystx.hytoracloud.driver.cloudservices.managing.command.command;


import de.lystx.hytoracloud.driver.cloudservices.managing.command.base.CommandUsage;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public class CommandInfo {

    /**
     * The name of the command
     */
    private final String name;

    /**
     * The description to give info
     */
    private final String description;

    /**
     * Aliases to access this command
     */
    private final String[] aliases;

    /**
     * The usage for this command
     */
    private final CommandUsage commandUsage;
}
