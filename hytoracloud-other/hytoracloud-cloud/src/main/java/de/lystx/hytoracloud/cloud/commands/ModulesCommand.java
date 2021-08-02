package de.lystx.hytoracloud.cloud.commands;

import de.lystx.hytoracloud.cloud.CloudSystem;
import de.lystx.hytoracloud.driver.cloudservices.cloud.module.cloud.DriverModule;
import de.lystx.hytoracloud.driver.cloudservices.managing.command.base.CommandExecutor;
import de.lystx.hytoracloud.driver.cloudservices.managing.command.base.Command;
import de.lystx.hytoracloud.driver.cloudservices.cloud.module.cloud.ModuleService;

import java.util.Arrays;

public class ModulesCommand {


    @Command(name = "modules", description = "Manages modules", aliases = {"pl", "plugins"})
    public void execute(CommandExecutor sender, String[] args) {
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("list")) {
                if (CloudSystem.getInstance().getInstance(ModuleService.class).getDriverModules().isEmpty()) {
                    sender.sendMessage("ERROR", "§cThere are no modules at the moment!");
                    return;
                }
                sender.sendMessage("INFO", "§bModules§7:");
                for (DriverModule driverModule : CloudSystem.getInstance().getInstance(ModuleService.class).getDriverModules()) {
                    sender.sendMessage("INFO", "§9" + driverModule.getBase().getName() + " §7| §bVersion " + driverModule.getBase().getVersion() + " §7| §bAuthor " + Arrays.toString(driverModule.getBase().getAuthor()));
                }
            } else if (args[0].equalsIgnoreCase("rl")) {
                CloudSystem.getInstance().getInstance(ModuleService.class).shutdown(() -> {
                    CloudSystem.getInstance().getInstance(ModuleService.class).load();
                    sender.sendMessage("INFO", "§aThe modules were §2reloaded§a!");
                });
            } else {
                this.correctSyntax(sender);
            }
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("rl")) {
                String module = args[1];
                DriverModule finalDriverModule = CloudSystem.getInstance().getInstance(ModuleService.class).getModule(module);
                if (finalDriverModule == null) {
                    sender.sendMessage("ERROR", "§cThe module §e" + module + " §cseems not to exist!");
                    return;
                }
                sender.sendMessage("INFO", "§aThe module §2" + finalDriverModule.getBase().getName() + " §awas §2reloaded§a!");
            } else {
                this.correctSyntax(sender);
            }
        } else {
            this.correctSyntax(sender);
        }
    }

    public void correctSyntax(CommandExecutor sender) {
        sender.sendMessage("INFO", "§9Help for §bModules§7:");
        sender.sendMessage("INFO", "§9modules <list> §7| Lists all modules");
        sender.sendMessage("INFO", "§9players rl (module) §7| Reloads all modules");
    }
}
