package de.lystx.cloudsystem.global.commands;

import de.lystx.cloudsystem.cloud.CloudSystem;
import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.elements.service.ServiceGroup;
import de.lystx.cloudsystem.library.service.command.CloudCommand;
import de.lystx.cloudsystem.library.service.command.TabCompletable;
import de.lystx.cloudsystem.library.service.console.CloudConsole;
import de.lystx.cloudsystem.library.service.server.impl.GroupService;

import java.util.LinkedList;
import java.util.List;

public class RunCommand extends CloudCommand implements TabCompletable {


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
            cloudLibrary.getService().startService(serviceGroup);
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
                    cloudLibrary.getService().startService(serviceGroup);
                }
            } catch (NumberFormatException e) {
                console.getLogger().sendMessage("ERROR", "§cPlease provide a valid number!");
            }
        } else {
            console.getLogger().sendMessage("ERROR", "§crun <group> <number>");
        }
    }

    @Override
    public List<String> onTabComplete(CloudLibrary cloudLibrary, String[] args) {
        List<String> list = new LinkedList<>();
        for (ServiceGroup globalService : cloudLibrary.getService(GroupService.class).getGroups()) {
            list.add(globalService.getName());
        }
        return list;
    }
}
