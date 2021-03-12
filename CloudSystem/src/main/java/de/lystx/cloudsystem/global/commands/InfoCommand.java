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
                    sender.sendMessage("INFO", "Online Servers : ");
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
                    sender.sendMessage("INFO", "Groups: ");
                    for (ServiceGroup serverGroupMeta : cloudInstance.getService(GroupService.class).getGroups())
                        sender.sendMessage("INFO", "NAME: " + serverGroupMeta.getName() + " | TEMPLATE: " + serverGroupMeta.getTemplate());
                    return;
                case "proxys":
                    sender.sendMessage("INFO", "Proxies: ");
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
            DecimalFormat format = new DecimalFormat("##.#");
            NetworkInfo networkInfo = new NetworkInfo();
            networkInfo.calculate();

            sender.sendMessage("INFO", "§7----------------------------------");
            sender.sendMessage("INFO", "§bVersion §a: §f" + Updater.getCloudVersion());
            sender.sendMessage("INFO", "§bCPU-Usage §a: §f" + format.format(networkInfo.getCPUUsage()) + "%");
            sender.sendMessage("INFO", "§bInternal CPU-Usage §a: §f" + format.format(networkInfo.getInternalCPUUsage()) + "%");
            sender.sendMessage("INFO", "§bServer CPU §f: §a" + format.format(networkInfo.getUsedCPU()) + "%§h/§c" + format.format(networkInfo.getFreeCPU()));
            sender.sendMessage("INFO", "§bServer Memory §f: §a" + format.format(networkInfo.getUsedMemory()) + "§h/§c" + format.format(networkInfo.getFreeMemory()) + " §h(§eTotal§8h: §7" + format.format(networkInfo.getTotalMemory()) + "§h)");

            sender.sendMessage("INFO", "§bTPS §a: §f" + networkInfo.formatTps(cloudInstance.getTicksPerSecond().getTPS()));
            sender.sendMessage("INFO", "§bConnection §a: §f" + cloudInstance.getService(ConfigService.class).getNetworkConfig().getHost() + ":" + cloudInstance.getService(ConfigService.class).getNetworkConfig().getPort());
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
