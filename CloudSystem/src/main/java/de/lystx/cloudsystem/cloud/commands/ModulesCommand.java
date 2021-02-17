package de.lystx.cloudsystem.cloud.commands;

import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.service.command.CloudCommand;
import de.lystx.cloudsystem.library.service.console.CloudConsole;
import de.lystx.cloudsystem.library.service.module.Module;
import de.lystx.cloudsystem.library.service.module.ModuleService;
import de.lystx.cloudsystem.library.service.scheduler.Scheduler;

public class ModulesCommand extends CloudCommand {

    public ModulesCommand(String name, String description, String... aliases) {
        super(name, description, aliases);
    }

    @Override
    public void execute(CloudLibrary cloudLibrary, CloudConsole console, String command, String[] args) {
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("list")) {
                if (cloudLibrary.getService(ModuleService.class).getModules().isEmpty()) {
                    console.getLogger().sendMessage("ERROR", "§cThere are no modules at the moment!");
                    return;
                }
                console.getLogger().sendMessage("INFO", "§bModules§7:");
                for (Module module : cloudLibrary.getService(ModuleService.class).getModules()) {
                    console.getLogger().sendMessage("INFO", "§9" + module.getInfo().getName() + " §7| §bVersion " + module.getInfo().getVersion() + " §7| §bAuthor " + module.getInfo().getAuthor());
                }
            } else if (args[0].equalsIgnoreCase("rl")) {
                cloudLibrary.getService(ModuleService.class).shutdown();
                cloudLibrary.getService(ModuleService.class).load();
                console.getLogger().sendMessage("INFO", "§aThe modules were §2reloaded§a!");
            } else {
                this.correctSyntax(console);
            }
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("rl")) {
                String module = args[1];
                Module finalModule = cloudLibrary.getService(ModuleService.class).getModule(module);
                if (finalModule == null) {
                    console.getLogger().sendMessage("ERROR", "§cThe module §e" + module + " §cseems not to exist!");
                    return;
                }
                finalModule.onDisable(cloudLibrary);
                cloudLibrary.getService(Scheduler.class).scheduleDelayedTask(() -> finalModule.onEnable(cloudLibrary), 5L);
                console.getLogger().sendMessage("INFO", "§aThe module §2" + finalModule.getInfo().getName() + " §awas §2reloaded§a!");
            } else {
                this.correctSyntax(console);
            }
        } else {
            this.correctSyntax(console);
        }
    }

    @Override
    public void correctSyntax(CloudConsole console) {
        console.getLogger().sendMessage("INFO", "§9Help for §bModules§7:");
        console.getLogger().sendMessage("INFO", "§9modules <list> §7| Lists all modules");
        console.getLogger().sendMessage("INFO", "§9players rl (module) §7| Reloads all modules");
    }
}
