package de.lystx.cloudsystem.global.commands;

import de.lystx.cloudsystem.cloud.CloudSystem;
import de.lystx.cloudsystem.global.CloudInstance;
import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.elements.service.ServiceGroup;
import de.lystx.cloudsystem.library.service.command.base.CloudCommandSender;
import de.lystx.cloudsystem.library.service.command.base.Command;
import de.lystx.cloudsystem.library.service.command.command.TabCompletable;
import de.lystx.cloudsystem.library.service.server.impl.GroupService;
import lombok.AllArgsConstructor;

import java.util.LinkedList;
import java.util.List;

@AllArgsConstructor
public class RunCommand implements TabCompletable {

    private final CloudInstance cloudInstance;

    @Command(name = "run", description = "Starts new services", aliases = "start")
    public void execute(CloudCommandSender sender, String[] args) {
        if (args.length == 1) {
            String group = args[0];
            ServiceGroup serviceGroup = cloudInstance.getService(GroupService.class).getGroup(group);
            if (serviceGroup == null) {
                sender.sendMessage("ERROR", "§cThe ServiceGroup §e" + group + " §cseems not to exist!");
                return;
            }
            cloudInstance.getService().startService(serviceGroup);
        } else if (args.length == 2) {
            try {
                int id = Integer.parseInt(args[1]);
                String group = args[0];
                ServiceGroup serviceGroup = cloudInstance.getService(GroupService.class).getGroup(group);
                if (serviceGroup == null) {
                    sender.sendMessage("ERROR", "§cThe ServiceGroup §e" + group + " §cseems not to exist!");
                    return;
                }
                for (int i = 0; i < id; i++) {
                    cloudInstance.getService().startService(serviceGroup);
                }
            } catch (NumberFormatException e) {
                sender.sendMessage("ERROR", "§cPlease provide a valid number!");
            }
        } else {
            sender.sendMessage("ERROR", "§crun <group> <number>");
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
