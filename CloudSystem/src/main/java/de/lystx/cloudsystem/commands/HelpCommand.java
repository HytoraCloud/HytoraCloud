package de.lystx.cloudsystem.commands;

import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.service.command.Command;
import de.lystx.cloudsystem.library.service.command.CommandService;
import de.lystx.cloudsystem.library.service.console.CloudConsole;

import java.util.Arrays;

public class HelpCommand extends Command {


    public HelpCommand(String name, String description, String... aliases) {
        super(name, description, aliases);
    }

    @Override
    public void execute(CloudLibrary cloudLibrary, CloudConsole console, String command, String[] args) {
        for (Command command1 : cloudLibrary.getService(CommandService.class).getCommands()) {
            console.getLogger().sendMessage("COMMAND", "§b" + command1.getName() + " §7| §a" + command1.getDescription() + " §7| §2" + Arrays.toString(command1.getAliases()));
        }
    }
}
