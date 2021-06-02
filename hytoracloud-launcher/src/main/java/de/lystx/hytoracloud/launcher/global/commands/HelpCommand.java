package de.lystx.hytoracloud.launcher.global.commands;

import de.lystx.hytoracloud.launcher.global.CloudProcess;
import de.lystx.hytoracloud.driver.service.command.command.CommandInfo;
import de.lystx.hytoracloud.driver.service.command.base.CloudCommandSender;
import de.lystx.hytoracloud.driver.service.command.base.Command;
import de.lystx.hytoracloud.driver.service.command.CommandService;
import lombok.AllArgsConstructor;

import java.util.Arrays;

@AllArgsConstructor
public class HelpCommand {


    private final CloudProcess cloudInstance;
    
    @Command(name = "help", description = "Shows you this message", aliases = {"?", "whattodo"})
    public void execute(CloudCommandSender sender, String[] args) {
        for (CommandInfo commandInfo1 : cloudInstance.getInstance(CommandService.class).getCommandInfos()) {
            sender.sendMessage("COMMAND", "§b" + commandInfo1.getName() + " §7| §a" + commandInfo1.getDescription() + " §7| §2" + Arrays.toString(commandInfo1.getAliases()));
        }
    }
}
