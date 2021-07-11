package de.lystx.hytoracloud.launcher.cloud.commands;

import de.lystx.hytoracloud.launcher.cloud.CloudSystem;
import de.lystx.hytoracloud.driver.service.managing.command.base.CloudCommandSender;
import de.lystx.hytoracloud.driver.service.managing.command.base.Command;
import de.lystx.hytoracloud.driver.service.cloud.module.Module;
import de.lystx.hytoracloud.driver.service.cloud.module.ModuleService;
import de.lystx.hytoracloud.driver.utils.scheduler.Scheduler;

public class ModulesCommand {


    @Command(name = "modules", description = "Manages modules", aliases = {"pl", "plugins"})
    public void execute(CloudCommandSender sender, String[] args) {
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("list")) {
                if (CloudSystem.getInstance().getInstance(ModuleService.class).getModules().isEmpty()) {
                    sender.sendMessage("ERROR", "§cThere are no modules at the moment!");
                    return;
                }
                sender.sendMessage("INFO", "§bModules§7:");
                for (Module module : CloudSystem.getInstance().getInstance(ModuleService.class).getModules()) {
                    sender.sendMessage("INFO", "§9" + module.getInfo().getName() + " §7| §bVersion " + module.getInfo().getVersion() + " §7| §bAuthor " + module.getInfo().getAuthor());
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
                Module finalModule = CloudSystem.getInstance().getInstance(ModuleService.class).getModule(module);
                if (finalModule == null) {
                    sender.sendMessage("ERROR", "§cThe module §e" + module + " §cseems not to exist!");
                    return;
                }
                finalModule.onDisable();
                CloudSystem.getInstance().getInstance(Scheduler.class).scheduleDelayedTask(finalModule::onEnable, 5L);
                sender.sendMessage("INFO", "§aThe module §2" + finalModule.getInfo().getName() + " §awas §2reloaded§a!");
            } else {
                this.correctSyntax(sender);
            }
        } else {
            this.correctSyntax(sender);
        }
    }

    public void correctSyntax(CloudCommandSender sender) {
        sender.sendMessage("INFO", "§9Help for §bModules§7:");
        sender.sendMessage("INFO", "§9modules <list> §7| Lists all modules");
        sender.sendMessage("INFO", "§9players rl (module) §7| Reloads all modules");
    }
}
