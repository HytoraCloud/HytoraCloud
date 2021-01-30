package de.lystx.cloudsystem.library.service.command;


import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.service.console.CloudConsole;
import lombok.Getter;

@Getter
public abstract class CloudCommand {

    private final String name;
    private final String description;
    private final String[] aliases;

    public CloudCommand(String name, String description, String... aliases) {
        this.name = name;
        this.description = description;
        this.aliases = aliases;
    }

    public abstract void execute(CloudLibrary cloudLibrary, CloudConsole console, String command, String[] args);


    public void correctSyntax(CloudConsole console) {}
}
