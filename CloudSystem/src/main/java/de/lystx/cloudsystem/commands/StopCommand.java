package de.lystx.cloudsystem.commands;

import de.lystx.cloudsystem.CloudSystem;
import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.elements.service.Service;
import de.lystx.cloudsystem.library.elements.service.ServiceGroup;
import de.lystx.cloudsystem.library.service.command.Command;
import de.lystx.cloudsystem.library.service.console.CloudConsole;
import de.lystx.cloudsystem.library.service.server.impl.GroupService;

public class StopCommand extends Command {


    public StopCommand(String name, String description, String... aliases) {
        super(name, description, aliases);
    }

    @Override
    public void execute(CloudLibrary cloudLibrary, CloudConsole console, String command, String[] args) {
        if (args.length == 1) {
            String s = args[0];
            Service service = CloudSystem.getInstance().getService().getService(s);
            if (service == null) {
                console.getLogger().sendMessage("ERROR", "§cThe service §e" + s + " §cseems not to be online!");
                return;
            }
            CloudSystem.getInstance().getService().stopService(service);
            //console.getLogger().sendMessage("COMMAND", "§7The service §a" + service.getName() + " §7| §bGroup " + service.getServiceGroup().getName() + " §7was stopped§8!");
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("group")) {
                String groupName = args[1];
                ServiceGroup group = cloudLibrary.getService(GroupService.class).getGroup(groupName);
                if (group == null) {
                    console.getLogger().sendMessage("ERROR", "§cThe group §e" + groupName + " §cseems not to exist!");
                    return;
                }
                CloudSystem.getInstance().getService().stopServices(group);
                console.getLogger().sendMessage("COMMAND", "§7The group §a" + group.getName() + " §7was stopped§8!");
            } else {
                console.getLogger().sendMessage("ERROR", "§cstop <group <servicename>/servicename>");
            }
        } else {
            console.getLogger().sendMessage("ERROR", "§cstop <group <servicename>/servicename>");
        }
    }
}
