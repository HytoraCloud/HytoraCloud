package de.lystx.hytoracloud.launcher.global.commands;

import de.lystx.hytoracloud.launcher.global.CloudProcess;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.commons.service.IServiceGroup;
import de.lystx.hytoracloud.driver.cloudservices.managing.command.base.CloudCommandSender;
import de.lystx.hytoracloud.driver.cloudservices.managing.command.base.Command;
import de.lystx.hytoracloud.driver.cloudservices.managing.command.command.TabCompletable;
import de.lystx.hytoracloud.driver.cloudservices.cloud.server.impl.GroupService;
import lombok.AllArgsConstructor;

import java.util.LinkedList;
import java.util.List;

@AllArgsConstructor
public class RunCommand implements TabCompletable {

    private final CloudProcess cloudInstance;

    @Command(name = "run", description = "Starts new services", aliases = "start")
    public void execute(CloudCommandSender sender, String[] args) {
        if (args.length == 1) {
            String group = args[0];
            IServiceGroup IServiceGroup = cloudInstance.getInstance(GroupService.class).getGroup(group);
            if (IServiceGroup == null) {
                sender.sendMessage("ERROR", "§cThe ServiceGroup §e" + group + " §cseems not to exist!");
                return;
            }
            CloudDriver.getInstance().getServiceManager().startService(IServiceGroup);
        } else if (args.length == 2) {
            try {
                int id = Integer.parseInt(args[1]);
                String group = args[0];
                IServiceGroup IServiceGroup = cloudInstance.getInstance(GroupService.class).getGroup(group);
                if (IServiceGroup == null) {
                    sender.sendMessage("ERROR", "§cThe ServiceGroup §e" + group + " §cseems not to exist!");
                    return;
                }
                for (int i = 0; i < id; i++) {
                    CloudDriver.getInstance().getServiceManager().startService(IServiceGroup);
                }
            } catch (NumberFormatException e) {
                sender.sendMessage("ERROR", "§cPlease provide a valid number!");
            }
        } else {
            sender.sendMessage("ERROR", "§crun <group> <number>");
        }
    }

    @Override
    public List<String> onTabComplete(CloudDriver cloudDriver, String[] args) {
        List<String> list = new LinkedList<>();
        for (IServiceGroup globalService : cloudDriver.getInstance(GroupService.class).getGroups()) {
            list.add(globalService.getName());
        }
        return list;
    }
}
