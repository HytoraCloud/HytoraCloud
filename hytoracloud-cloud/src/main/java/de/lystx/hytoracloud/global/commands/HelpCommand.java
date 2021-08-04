package de.lystx.hytoracloud.global.commands;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.command.execution.ICommand;
import de.lystx.hytoracloud.driver.command.executor.CommandExecutor;
import de.lystx.hytoracloud.driver.command.execution.CommandInfo;
import de.lystx.hytoracloud.driver.command.execution.CommandListener;
import de.lystx.hytoracloud.driver.utils.other.Array;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@CommandInfo(name = "help", description = "Shows you this message", aliases = {"?", "whattodo"})
public class HelpCommand implements CommandListener {

    @Override
    public void execute(CommandExecutor sender, String[] args) {
        sender.sendMessage("INFO", "§7All registered §bCommands §h[§3" + CloudDriver.getInstance().getCommandManager().getCommands().size() + "§h]:");
        for (ICommand command : CloudDriver.getInstance().getCommandManager().getCommands()) {
            Array<String> array = new Array<>(command.getAliases());
            sender.sendMessage("INFO", "§h» §b" + command.getName() + " §h| §7" + command.getDescription() + " §h| §7" + (array.size() == 1 ? "Alias" : "Aliases") + " " + (array.size() == 0 ? "§cNone" : "§3" + array.toStringWithChars()));
        }
        sender.sendMessage("§8");
    }
}
