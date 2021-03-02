package de.lystx.cloudsystem.global.commands;

import de.lystx.cloudsystem.cloud.CloudSystem;
import de.lystx.cloudsystem.global.CloudInstance;
import de.lystx.cloudsystem.library.service.command.command.CommandInfo;
import de.lystx.cloudsystem.library.service.command.base.CloudCommandSender;
import de.lystx.cloudsystem.library.service.command.base.Command;
import de.lystx.cloudsystem.library.service.command.CommandService;
import lombok.AllArgsConstructor;

import java.util.Arrays;

@AllArgsConstructor
public class HelpCommand {


    private final CloudInstance cloudInstance;
    
    @Command(name = "help", description = "Shows you this message", aliases = {"?", "whattodo"})
    public void execute(CloudCommandSender sender, String[] args) {
        for (CommandInfo commandInfo1 : cloudInstance.getService(CommandService.class).getCommandInfos()) {
            sender.sendMessage("COMMAND", "§b" + commandInfo1.getName() + " §7| §a" + commandInfo1.getDescription() + " §7| §2" + Arrays.toString(commandInfo1.getAliases()));
        }
    }
}
