package de.lystx.cloudsystem.global.commands;

import de.lystx.cloudsystem.cloud.CloudSystem;
import de.lystx.cloudsystem.global.CloudInstance;
import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.elements.service.Service;
import de.lystx.cloudsystem.library.elements.service.ServiceGroup;
import de.lystx.cloudsystem.library.service.command.base.CloudCommandSender;
import de.lystx.cloudsystem.library.service.command.base.Command;
import de.lystx.cloudsystem.library.service.command.command.TabCompletable;
import de.lystx.cloudsystem.library.service.server.impl.GroupService;
import de.lystx.cloudsystem.library.service.server.other.ServerService;
import lombok.AllArgsConstructor;

import java.util.LinkedList;
import java.util.List;

@AllArgsConstructor
public class StopCommand implements TabCompletable {

    private final CloudInstance cloudInstance;

    @Command(name = "stop", description = "Stops a service or group")
    public void execute(CloudCommandSender sender, String[] args) {
        if (args.length == 1) {
            String s = args[0];
            Service service = cloudInstance.getService().getService(s);
            if (service == null) {
                sender.sendMessage("ERROR", "§cThe service §e" + s + " §cseems not to be online!");
                return;
            }
            cloudInstance.getService().stopService(service);
            //sender.sendMessage("COMMAND", "§7The service §a" + service.getName() + " §7| §bGroup " + service.getServiceGroup().getName() + " §7was stopped§8!");
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("group")) {
                String groupName = args[1];
                ServiceGroup group = cloudInstance.getService(GroupService.class).getGroup(groupName);
                if (group == null) {
                    sender.sendMessage("ERROR", "§cThe group §e" + groupName + " §cseems not to exist!");
                    return;
                }
                sender.sendMessage("COMMAND", "§7The group §a" + group.getName() + " §7was stopped§8!");
                cloudInstance.getService().stopServices(group);
            } else {
                sender.sendMessage("ERROR", "§cstop <group <groupname>/servicename>");
            }
        } else {
            sender.sendMessage("ERROR", "§cstop <group <groupname>/servicename>");
        }
    }

    @Override
    public List<String> onTabComplete(CloudLibrary cloudLibrary, String[] args) {
        List<String> list = new LinkedList<>();
        if (args.length == 2) {
            list.add("group");
            for (Service globalService : cloudLibrary.getService(ServerService.class).getGlobalServices()) {
                if (cloudLibrary.getService(ServerService.class).getService(globalService.getName()) == null) {
                    continue;
                }
                list.add(globalService.getName());
            }
        } else if (args.length == 3 && args[1].equalsIgnoreCase("group")) {
            for (ServiceGroup globalService : cloudLibrary.getService(GroupService.class).getGroups()) {
                if (cloudLibrary.getService(ServerService.class).getService(globalService.getName()) == null) {
                    continue;
                }
                list.add(globalService.getName());
            }
        }
        return list;
    }
}
