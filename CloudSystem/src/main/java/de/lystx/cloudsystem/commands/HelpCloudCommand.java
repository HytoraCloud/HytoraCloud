package de.lystx.cloudsystem.commands;

import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.service.command.CloudCommand;
import de.lystx.cloudsystem.library.service.command.CommandService;
import de.lystx.cloudsystem.library.service.console.CloudConsole;

import java.util.Arrays;

public class HelpCloudCommand extends CloudCommand {


    public HelpCloudCommand(String name, String description, String... aliases) {
        super(name, description, aliases);
    }

    @Override
    public void execute(CloudLibrary cloudLibrary, CloudConsole console, String command, String[] args) {
        for (CloudCommand cloudCommand1 : cloudLibrary.getService(CommandService.class).getCloudCommands()) {
            console.getLogger().sendMessage("COMMAND", "§b" + cloudCommand1.getName() + " §7| §a" + cloudCommand1.getDescription() + " §7| §2" + Arrays.toString(cloudCommand1.getAliases()));
        }
    }
}
