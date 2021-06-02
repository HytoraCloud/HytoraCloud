package de.lystx.hytoracloud.driver.service.command.command;


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

}
