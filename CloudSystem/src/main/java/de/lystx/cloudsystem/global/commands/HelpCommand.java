package de.lystx.cloudsystem.global.commands;

import de.lystx.cloudsystem.cloud.CloudSystem;
import de.lystx.cloudsystem.library.service.command.command.CommandInfo;
import de.lystx.cloudsystem.library.service.command.base.CloudCommandSender;
import de.lystx.cloudsystem.library.service.command.base.Command;
import de.lystx.cloudsystem.library.service.command.CommandService;

import java.util.Arrays;

public class HelpCommand {


    @Command(name = "help", description = "Shows you this message", aliases = {"?", "whattodo"})
    public void execute(CloudCommandSender sender, String[] args) {
        for (CommandInfo commandInfo1 : CloudSystem.getInstance().getService(CommandService.class).getCommandInfos()) {
            sender.sendMessage("COMMAND", "§b" + commandInfo1.getName() + " §7| §a" + commandInfo1.getDescription() + " §7| §2" + Arrays.toString(commandInfo1.getAliases()));
        }
    }
}
