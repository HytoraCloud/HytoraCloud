package de.lystx.cloudsystem.library.service.command.command;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public final class CommandInfo {

    private final String name;
    private final String description;
    private final String[] aliases;

}
