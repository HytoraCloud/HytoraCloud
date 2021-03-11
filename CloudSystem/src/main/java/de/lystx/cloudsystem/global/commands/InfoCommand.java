package de.lystx.cloudsystem.global.commands;


import de.lystx.cloudsystem.global.CloudInstance;
import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.elements.featured.updater.Updater;
import de.lystx.cloudsystem.library.elements.service.Service;
import de.lystx.cloudsystem.library.elements.service.ServiceGroup;
import de.lystx.cloudsystem.library.elements.service.ServiceType;
import de.lystx.cloudsystem.library.service.command.base.CloudCommandSender;
import de.lystx.cloudsystem.library.service.command.base.Command;
import de.lystx.cloudsystem.library.service.command.command.TabCompletable;
import de.lystx.cloudsystem.library.service.config.ConfigService;
import de.lystx.cloudsystem.library.service.server.impl.GroupService;
import de.lystx.cloudsystem.library.service.util.NetworkInfo;
import lombok.AllArgsConstructor;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

@AllArgsConstructor
public class InfoCommand implements TabCompletable {

    private final CloudInstance cloudInstance;

    @Command(name = "info", description = "Shows information", aliases = "information")
    public void execute(CloudCommandSender sender, String[] args) {
        if (args.length == 1) {
            switch (args[0]) {
                case "servers":
                    sender.sendMessage("INFO", "SERVERS (ONLINE) : ");
                    for (Service serverMeta : cloudInstance.getService().getGlobalServices()) {
                        if (cloudInstance.getService().getService(serverMeta.getName()) == null) {
                            continue;
                        }
                        if (serverMeta.getServiceGroup().getServiceType().equals(ServiceType.PROXY)) {
                            continue;
                        }
                        sender.sendMessage("INFO", "NAME: " + serverMeta.getName() + " | GROUP: " + serverMeta.getServiceGroup().getName() + " | TEMPLATE: " + serverMeta.getServiceGroup().getTemplate());
                    }
                    return;
                case "groups":
                    sender.sendMessage("INFO", "GROUPS: ");
                    for (ServiceGroup serverGroupMeta : cloudInstance.getService(GroupService.class).getGroups())
                        sender.sendMessage("INFO", "NAME: " + serverGroupMeta.getName() + " | TEMPLATE: " + serverGroupMeta.getTemplate());
                    return;
                case "proxys":
                    sender.sendMessage("INFO", "PROXYS: ");
                    for (Service serverMeta : cloudInstance.getService().getGlobalServices()) {
                        if (cloudInstance.getService().getService(serverMeta.getName()) == null) {
                            continue;
                        }
                        if (serverMeta.getServiceGroup().getServiceType().equals(ServiceType.SPIGOT)) {
                            continue;
                        }
                        sender.sendMessage("INFO", "NAME: " + serverMeta.getName() + " | GROUP: " + serverMeta.getServiceGroup().getName() + " | TEMPLATE: " + serverMeta.getServiceGroup().getTemplate());
                    }
            }
        } else {
            NetworkInfo networkInfo = new NetworkInfo();
            DecimalFormat format = new DecimalFormat("##.#");
            sender.sendMessage("INFO", "§7----------------------------------");
            sender.sendMessage("INFO", "§bNewest version §a: §f" + (Updater.isUpToDate() ? "§aYes": "§cNo"));
            sender.sendMessage("INFO", "§bVersion §a: §f" + Updater.getCloudVersion());
            sender.sendMessage("INFO", "§bCPU-Usage §a: §f" + format.format(networkInfo.getCPUUsage()) + "%");
            sender.sendMessage("INFO", "§bSystem Memory §a: §f" + format.format(networkInfo.getSystemMemory()) + "/?");
            sender.sendMessage("INFO", "§bInternal CPU-Usage §a: §f" + format.format(networkInfo.getInternalCPUUsage()) + "%");
            sender.sendMessage("INFO", "§bMX OS §a: §f" + networkInfo.getOperatingSystemMX().getName());
            sender.sendMessage("INFO", "§bTPS §a: §f" + networkInfo.formatTps(cloudInstance.getTicksPerSecond().getTPS()));
            sender.sendMessage("INFO", "§bCloud-Host §a: §f" + cloudInstance.getService(ConfigService.class).getNetworkConfig().getHost());
            sender.sendMessage("INFO", "§bCloud-Port §a: §f" + cloudInstance.getService(ConfigService.class).getNetworkConfig().getPort());
            sender.sendMessage("INFO", "§7----------------------------------");
            sender.sendMessage("INFO", "§9info <servers> §7| §bLists all servers");
            sender.sendMessage("INFO", "§9info <groups> §7| §bLists all groups");
            sender.sendMessage("INFO", "§9info <proxys> §7| §bLists all proxys");
            sender.sendMessage("INFO", "§9info §7| §bLists all infos");
        }
    }

    @Override
    public List<String> onTabComplete(CloudLibrary cloudLibrary, String[] args) {
        return Arrays.asList("servers", "groups", "proxys");
    }
}
