package de.lystx.cloudsystem.global.commands;


import de.lystx.cloudsystem.cloud.CloudSystem;
import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.Updater;
import de.lystx.cloudsystem.library.elements.service.Service;
import de.lystx.cloudsystem.library.elements.service.ServiceGroup;
import de.lystx.cloudsystem.library.elements.service.ServiceType;
import de.lystx.cloudsystem.library.service.command.CloudCommand;
import de.lystx.cloudsystem.library.service.command.TabCompletable;
import de.lystx.cloudsystem.library.service.config.ConfigService;
import de.lystx.cloudsystem.library.service.console.CloudConsole;
import de.lystx.cloudsystem.library.service.server.impl.GroupService;
import de.lystx.cloudsystem.library.service.util.NetworkInfo;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

public class InfoCommand extends CloudCommand implements TabCompletable {

    public InfoCommand(String name, String description, String... aliases) {
        super(name, description, aliases);
    }

    public void execute(CloudLibrary cloudLibrary, CloudConsole console, String command, String[] args) {
        if (args.length == 1) {
            switch (args[0]) {
                case "servers":
                    console.getLogger().sendMessage("INFO", "SERVERS (ONLINE) : ");
                    for (Service serverMeta : cloudLibrary.getService().getGlobalServices()) {
                        if (cloudLibrary.getService().getService(serverMeta.getName()) == null) {
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
                    for (ServiceGroup serverGroupMeta : cloudLibrary.getService(GroupService.class).getGroups())
                        console.getLogger().sendMessage("INFO", "NAME: " + serverGroupMeta.getName() + " | TEMPLATE: " + serverGroupMeta.getTemplate());
                    return;
                case "proxys":
                    console.getLogger().sendMessage("INFO", "PROXYS: ");
                    for (Service serverMeta : cloudLibrary.getService().getGlobalServices()) {
                        if (cloudLibrary.getService().getService(serverMeta.getName()) == null) {
                            continue;
                        }
                        if (serverMeta.getServiceGroup().getServiceType().equals(ServiceType.SPIGOT)) {
                            continue;
                        }
                        console.getLogger().sendMessage("INFO", "NAME: " + serverMeta.getName() + " | GROUP: " + serverMeta.getServiceGroup().getName() + " | TEMPLATE: " + serverMeta.getServiceGroup().getTemplate());
                    }
            }
        } else {
            NetworkInfo networkInfo = new NetworkInfo();
            DecimalFormat format = new DecimalFormat("##.#");
            console.getLogger().sendMessage("INFO", "§7----------------------------------");
            console.getLogger().sendMessage("INFO", "§bNewest version §a: §f" + (Updater.isUpToDate() ? "§aYes": "§cNo"));
            console.getLogger().sendMessage("INFO", "§bVersion §a: §f" + Updater.getCloudVersion());
            console.getLogger().sendMessage("INFO", "§bCPU-Usage §a: §f" + format.format(networkInfo.getCPUUsage()) + "%");
            console.getLogger().sendMessage("INFO", "§bSystem Memory §a: §f" + format.format(networkInfo.getSystemMemory()) + "/?");
            console.getLogger().sendMessage("INFO", "§bInternal CPU-Usage §a: §f" + format.format(networkInfo.getInternalCPUUsage()) + "%");
            console.getLogger().sendMessage("INFO", "§bMX OS §a: §f" + networkInfo.getOperatingSystemMX().getName());
            console.getLogger().sendMessage("INFO", "§bTPS §a: §f" + networkInfo.formatTps(cloudLibrary.getTicksPerSecond().getTPS()));
            console.getLogger().sendMessage("INFO", "§bCloud-Host §a: §f" + cloudLibrary.getService(ConfigService.class).getNetworkConfig().getHost());
            console.getLogger().sendMessage("INFO", "§bCloud-Port §a: §f" + cloudLibrary.getService(ConfigService.class).getNetworkConfig().getPort());
            console.getLogger().sendMessage("INFO", "§7----------------------------------");
            console.getLogger().sendMessage("INFO", "§9info <servers> §7| §bLists all servers");
            console.getLogger().sendMessage("INFO", "§9info <groups> §7| §bLists all groups");
            console.getLogger().sendMessage("INFO", "§9info <proxys> §7| §bLists all proxys");
            console.getLogger().sendMessage("INFO", "§9info §7| §bLists all infos");
        }
    }

    @Override
    public List<String> onTabComplete(CloudLibrary cloudLibrary, String[] args) {
        return Arrays.asList("servers", "groups", "proxys");
    }
}
