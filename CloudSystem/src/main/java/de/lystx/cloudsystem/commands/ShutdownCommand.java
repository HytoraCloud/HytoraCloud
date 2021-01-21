package de.lystx.cloudsystem.commands;

import de.lystx.cloudsystem.CloudSystem;
import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.service.command.Command;
import de.lystx.cloudsystem.library.service.console.CloudConsole;
import de.lystx.cloudsystem.library.service.scheduler.Scheduler;

public class ShutdownCommand extends Command {


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
        CloudSystem.getInstance().shutdown();
    }
}
