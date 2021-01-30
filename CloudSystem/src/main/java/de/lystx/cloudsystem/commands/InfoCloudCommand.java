package de.lystx.cloudsystem.commands;


import de.lystx.cloudsystem.CloudSystem;
import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.elements.service.Service;
import de.lystx.cloudsystem.library.elements.service.ServiceGroup;
import de.lystx.cloudsystem.library.elements.service.ServiceType;
import de.lystx.cloudsystem.library.service.command.CloudCommand;
import de.lystx.cloudsystem.library.service.command.TabCompletable;
import de.lystx.cloudsystem.library.service.config.ConfigService;
import de.lystx.cloudsystem.library.service.console.CloudConsole;
import de.lystx.cloudsystem.library.service.server.impl.GroupService;

import java.util.Arrays;
import java.util.List;

public class InfoCloudCommand extends CloudCommand implements TabCompletable {

    public InfoCloudCommand(String name, String description, String... aliases) {
        super(name, description, aliases);
    }

    public void execute(CloudLibrary cloudLibrary, CloudConsole console, String command, String[] args) {
        if (args.length == 1) {
            switch (args[0]) {
                case "servers":
                    console.getLogger().sendMessage("INFO", "SERVERS (ONLINE) : ");
                    for (Service serverMeta : CloudSystem.getInstance().getService().getGlobalServices()) {
                        if (CloudSystem.getInstance().getService().getService(serverMeta.getName()) == null) {
                            continue;
                        }
                        if (serverMeta.getServiceGroup().getServiceType().equals(ServiceType.PROXY)) {
                            continue;
                        }
                        console.getLogger().sendMessage("INFO", "NAME: " + serverMeta.getName() + " | GROUP: " + serverMeta.getServiceGroup().getName() + " | TEMPLATE: " + serverMeta.getServiceGroup().getTemplate());
                    }
                    return;
                case "groups":
                    console.getLogger().sendMessage("INFO", "GROUPS: ");
                    for (ServiceGroup serverGroupMeta : CloudSystem.getInstance().getService(GroupService.class).getGroups())
                        console.getLogger().sendMessage("INFO", "NAME: " + serverGroupMeta.getName() + " | TEMPLATE: " + serverGroupMeta.getTemplate());
                    return;
                case "proxys":
                    console.getLogger().sendMessage("INFO", "PROXYS: ");
                    for (Service serverMeta : CloudSystem.getInstance().getService().getGlobalServices()) {
                        if (CloudSystem.getInstance().getService().getService(serverMeta.getName()) == null) {
                            continue;
                        }
                        if (serverMeta.getServiceGroup().getServiceType().equals(ServiceType.SPIGOT)) {
                            continue;
                        }
                        console.getLogger().sendMessage("INFO", "NAME: " + serverMeta.getName() + " | GROUP: " + serverMeta.getServiceGroup().getName() + " | TEMPLATE: " + serverMeta.getServiceGroup().getTemplate());
                    }
            }
        } else {
            console.getLogger().sendMessage("INFO", "§7----------------------------------");
            console.getLogger().sendMessage("INFO", "§9Version §a: §fV1.0");
            console.getLogger().sendMessage("INFO", "§9Developer §a: §fLystx");
            console.getLogger().sendMessage("INFO", "§9Host §a: §f" + CloudSystem.getInstance().getService(ConfigService.class).getNetworkConfig().getHost());
            console.getLogger().sendMessage("INFO", "§9Port §a: §f" + CloudSystem.getInstance().getService(ConfigService.class).getNetworkConfig().getPort());
            console.getLogger().sendMessage("INFO", "§9info <servers> §7| §bLists all servers");
            console.getLogger().sendMessage("INFO", "§9info <groups> §7| §bLists all groups");
            console.getLogger().sendMessage("INFO", "§9info <proxys> §7| §bLists all proxys");
            console.getLogger().sendMessage("INFO", "§9info §7| §bLists all infos");
            console.getLogger().sendMessage("§7----------------------------------");
        }
    }

    @Override
    public List<String> onTabComplete(CloudLibrary cloudLibrary, String[] args) {
        return Arrays.asList("servers", "groups", "proxys");
    }
}
