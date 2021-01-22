package de.lystx.cloudsystem.commands;

import de.lystx.cloudsystem.CloudSystem;
import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.elements.service.ServiceGroup;
import de.lystx.cloudsystem.library.service.command.Command;
import de.lystx.cloudsystem.library.service.console.CloudConsole;
import de.lystx.cloudsystem.library.service.server.impl.GroupService;

public class RunCommand extends Command {


    public RunCommand(String name, String description, String... aliases) {
        super(name, description, aliases);
    }

    @Override
    public void execute(CloudLibrary cloudLibrary, CloudConsole console, String command, String[] args) {
        if (args.length == 1) {
            String group = args[0];
            ServiceGroup serviceGroup = cloudLibrary.getService(GroupService.class).getGroup(group);
            if (serviceGroup == null) {
                console.getLogger().sendMessage("ERROR", "§cThe ServiceGroup §e" + group + " §cseems not to exist!");
                return;
            }
            CloudSystem.getInstance().getService().startService(serviceGroup);
        } else if (args.length == 2) {
            try {
                int id = Integer.parseInt(args[1]);
                String group = args[0];
                ServiceGroup serviceGroup = cloudLibrary.getService(GroupService.class).getGroup(group);
                if (serviceGroup == null) {
                    console.getLogger().sendMessage("ERROR", "§cThe ServiceGroup §e" + group + " §cseems not to exist!");
                    return;
                }
                for (int i = 0; i < id; i++) {
                    CloudSystem.getInstance().getService().startService(serviceGroup);
                }
            } catch (NumberFormatException e) {
                console.getLogger().sendMessage("ERROR", "§cPlease provide a valid number!");
            }
        } else {
            console.getLogger().sendMessage("ERROR", "§crun <group> <number>");
        }
    }
}
