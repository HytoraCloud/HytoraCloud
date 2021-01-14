package de.lystx.cloudsystem.commands;


import de.lystx.cloudsystem.CloudSystem;
import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.elements.packets.out.other.PacketPlayOutNetworkConfig;
import de.lystx.cloudsystem.library.service.command.Command;
import de.lystx.cloudsystem.library.service.config.ConfigService;
import de.lystx.cloudsystem.library.service.config.impl.NetworkConfig;
import de.lystx.cloudsystem.library.service.config.impl.proxy.ProxyConfig;
import de.lystx.cloudsystem.library.service.console.CloudConsole;
import de.lystx.cloudsystem.library.service.network.CloudNetworkService;

import java.util.List;

public class MaintenanceCommand extends Command {

    public MaintenanceCommand(String name, String description, String... aliases) {
        super(name, description, aliases);
    }

    public void execute(CloudLibrary cloudLibrary, CloudConsole colouredConsoleProvider, String command, String[] args) {
        NetworkConfig config = CloudSystem.getInstance().getService(ConfigService.class).getNetworkConfig();
        ProxyConfig proxyConfig = config.getProxyConfig();
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("switch")) {

                if (!proxyConfig.isMaintenance()) {
                    proxyConfig.setMaintenance(true);
                    config.setProxyConfig(proxyConfig);
                    CloudSystem.getInstance().getService(ConfigService.class).setNetworkConfig(config);
                    CloudSystem.getInstance().getService(ConfigService.class).save();
                    CloudSystem.getInstance().getService(CloudNetworkService.class).sendPacket(new PacketPlayOutNetworkConfig(config));
                    colouredConsoleProvider.getLogger().sendMessage("INFO", "§9The network is now in §amaintenance§9!");
                } else {
                    proxyConfig.setMaintenance(false);
                    config.setProxyConfig(proxyConfig);
                    CloudSystem.getInstance().getService(ConfigService.class).setNetworkConfig(config);
                    CloudSystem.getInstance().getService(ConfigService.class).save();
                    CloudSystem.getInstance().getService(CloudNetworkService.class).sendPacket(new PacketPlayOutNetworkConfig(config));
                    colouredConsoleProvider.getLogger().sendMessage("INFO", "§9The network is no longer in §cmaintenance§9!");
                }
            } else {
                sendUsage(colouredConsoleProvider);
            }
        } else if (args.length == 2) {
            String identifier = args[0];
            String user = args[1];
            List<String> whitelist = proxyConfig.getWhitelistedPlayers();
            if (identifier.equalsIgnoreCase("add")) {
                whitelist.add(user);
                proxyConfig.setWhitelistedPlayers(whitelist);
                config.setProxyConfig(proxyConfig);
                CloudSystem.getInstance().getService(ConfigService.class).setNetworkConfig(config);
                CloudSystem.getInstance().getService(ConfigService.class).save();
                CloudSystem.getInstance().getService(CloudNetworkService.class).sendPacket(new PacketPlayOutNetworkConfig(config));
                colouredConsoleProvider.getLogger().sendMessage("COMMAND", "§7The player §a" + user + " §7was added to maintenance§8!");
            } else if (identifier.equalsIgnoreCase("remove")) {
                whitelist.remove(user);
                proxyConfig.setWhitelistedPlayers(whitelist);
                config.setProxyConfig(proxyConfig);
                CloudSystem.getInstance().getService(ConfigService.class).setNetworkConfig(config);
                CloudSystem.getInstance().getService(ConfigService.class).save();
                CloudSystem.getInstance().getService(CloudNetworkService.class).sendPacket(new PacketPlayOutNetworkConfig(config));
                colouredConsoleProvider.getLogger().sendMessage("COMMAND", "§7The player §a" + user + " §7was removed to maintenance§8!");
            } else {
                sendUsage(colouredConsoleProvider);
            }
        } else {
            sendUsage(colouredConsoleProvider);
        }
    }

    private void sendUsage(CloudConsole colouredConsoleProvider) {
        colouredConsoleProvider.getLogger().sendMessage("INFO", "§9maintenance <add> <player> §7| §bAdds player to maintenance");
        colouredConsoleProvider.getLogger().sendMessage("INFO", "§9maintenance <remove> <player> §7| §bRemoves player from maintenance");
        colouredConsoleProvider.getLogger().sendMessage("INFO", "§9maintenance <switch> §7| §bToggles maintenance");
    }
}
