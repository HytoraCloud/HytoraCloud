package de.lystx.cloudsystem.commands;


import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.service.command.CloudCommand;
import de.lystx.cloudsystem.library.service.console.CloudConsole;

public class ClearCloudCommand extends CloudCommand {

    public ClearCloudCommand(String name, String description, String... aliases) {
        super(name, description, aliases);
    }

    @Override
    public void execute(CloudLibrary cloudLibrary, CloudConsole console, String command, String[] args) {
        console.clearScreen();
    }

}
