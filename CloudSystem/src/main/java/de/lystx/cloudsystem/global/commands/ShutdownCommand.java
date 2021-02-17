package de.lystx.cloudsystem.global.commands;

import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.service.command.CloudCommand;
import de.lystx.cloudsystem.library.service.console.CloudConsole;

public class ShutdownCommand extends CloudCommand {


    public ShutdownCommand(String name, String description, String... aliases) {
        super(name, description, aliases);
    }

    @Override
    public void execute(CloudLibrary cloudLibrary, CloudConsole console, String command, String[] args) {
        if (args.length != 0) {
            console.getLogger().sendMessage("ERROR", "§cPlease do not provide any arguments after §e<shutdown>§c!");
            return;
        }
        console.getLogger().sendMessage("COMMAND", "§cThe CloudSystem will §eshut down §cin 3 seconds...");
        cloudLibrary.shutdown();
    }
}
