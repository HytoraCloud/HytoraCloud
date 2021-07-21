package de.lystx.hytoracloud.launcher.cloud.commands;

import de.lystx.hytoracloud.driver.cloudservices.cloud.module.cloud.CloudModule;
import de.lystx.hytoracloud.launcher.cloud.CloudSystem;
import de.lystx.hytoracloud.driver.cloudservices.managing.command.base.CommandExecutor;
import de.lystx.hytoracloud.driver.cloudservices.managing.command.base.Command;
import de.lystx.hytoracloud.driver.cloudservices.cloud.module.cloud.ModuleService;
import de.lystx.hytoracloud.driver.cloudservices.global.scheduler.Scheduler;

public class ModulesCommand {


    @Command(name = "modules", description = "Manages modules", aliases = {"pl", "plugins"})
    public void execute(CommandExecutor sender, String[] args) {
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("list")) {
                if (CloudSystem.getInstance().getInstance(ModuleService.class).getCloudModules().isEmpty()) {
                    sender.sendMessage("ERROR", "§cThere are no modules at the moment!");
                    return;
                }
                sender.sendMessage("INFO", "§bModules§7:");
                for (CloudModule cloudModule : CloudSystem.getInstance().getInstance(ModuleService.class).getCloudModules()) {
                    sender.sendMessage("INFO", "§9" + cloudModule.getBase().getName() + " §7| §bVersion " + cloudModule.getBase().getVersion() + " §7| §bAuthor " + cloudModule.getBase().getAuthor());
                }
            } else if (args[0].equalsIgnoreCase("rl")) {
                CloudSystem.getInstance().getInstance(ModuleService.class).shutdown();
                CloudSystem.getInstance().getInstance(ModuleService.class).load();
                sender.sendMessage("INFO", "§aThe modules were §2reloaded§a!");
            } else {
                this.correctSyntax(sender);
            }
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("rl")) {
                String module = args[1];
                CloudModule finalCloudModule = CloudSystem.getInstance().getInstance(ModuleService.class).getModule(module);
                if (finalCloudModule == null) {
                    sender.sendMessage("ERROR", "§cThe module §e" + module + " §cseems not to exist!");
                    return;
                }
                finalCloudModule.onDisable();
                CloudSystem.getInstance().getInstance(Scheduler.class).scheduleDelayedTask(finalCloudModule::onEnable, 5L);
                sender.sendMessage("INFO", "§aThe module §2" + finalCloudModule.getBase().getName() + " §awas §2reloaded§a!");
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
