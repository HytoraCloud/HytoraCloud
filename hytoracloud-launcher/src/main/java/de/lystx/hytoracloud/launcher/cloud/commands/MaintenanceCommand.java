package de.lystx.hytoracloud.launcher.cloud.commands;


import de.lystx.hytoracloud.launcher.cloud.CloudSystem;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.cloudservices.managing.command.base.CloudCommandSender;
import de.lystx.hytoracloud.driver.cloudservices.managing.command.base.Command;
import de.lystx.hytoracloud.driver.cloudservices.managing.command.command.TabCompletable;
import de.lystx.hytoracloud.driver.cloudservices.global.config.ConfigService;
import de.lystx.hytoracloud.driver.cloudservices.global.config.impl.NetworkConfig;


import java.util.LinkedList;
import java.util.List;

public class MaintenanceCommand implements TabCompletable {

    @Command(name = "maintenance", description = "Manages maintenance of network", aliases = "mc")
    public void execute(CloudCommandSender sender, String[] args) {
        NetworkConfig config = CloudSystem.getInstance().getInstance(ConfigService.class).getNetworkConfig();
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("switch")) {
                if (!config.isMaintenance()) {
                    config.setMaintenance(true);
                    sender.sendMessage("INFO", "§9The network is now in §amaintenance§9!");
                } else {
                    config.setMaintenance(false);
                    sender.sendMessage("INFO", "§9The network is no longer in §cmaintenance§9!");
                }
                CloudSystem.getInstance().getInstance(ConfigService.class).setNetworkConfig(config);
                CloudSystem.getInstance().getInstance(ConfigService.class).shutdown();
                CloudSystem.getInstance().getInstance(ConfigService.class).reload();
            } else if (args[0].equalsIgnoreCase("list")) {
                sender.sendMessage("INFO", "§bWhitelisted Players§7:");
                for (String whitelistedPlayer : config.getWhitelistedPlayers()) {
                    sender.sendMessage("INFO", "§9" + whitelistedPlayer);
                }
            } else {
                sendUsage(sender);
            }
        } else if (args.length == 2) {
            String identifier = args[0];
            String user = args[1].trim();

            List<String> whitelist = config.getWhitelistedPlayers();
            boolean contains = whitelist.toString().toLowerCase().contains(user.toLowerCase());
            if (identifier.equalsIgnoreCase("add")) {
                if (contains) {
                    sender.sendMessage("ERROR", "§cThe player §e" + user + " §cis already added to maintenance!");
                    return;
                }
                whitelist.add(user);
                config.setWhitelistedPlayers(whitelist);
                CloudSystem.getInstance().getInstance(ConfigService.class).setNetworkConfig(config);
                CloudSystem.getInstance().getInstance(ConfigService.class).shutdown();
                CloudSystem.getInstance().getInstance(ConfigService.class).reload();
                sender.sendMessage("COMMAND", "§7The player §a" + user + " §7was added to maintenance§8!");
            } else if (identifier.equalsIgnoreCase("remove")) {
                if (!contains) {
                    sender.sendMessage("ERROR", "§cThe player §e" + user + " §cis not added to maintenance!");
                    return;
                }
                whitelist.remove(user);
                config.setWhitelistedPlayers(whitelist);
                CloudSystem.getInstance().getInstance(ConfigService.class).setNetworkConfig(config);
                CloudSystem.getInstance().getInstance(ConfigService.class).shutdown();
                CloudSystem.getInstance().getInstance(ConfigService.class).reload();
                sender.sendMessage("COMMAND", "§7The player §a" + user + " §7was removed to maintenance§8!");
            } else {
                sendUsage(sender);
            }
        } else {
            sendUsage(sender);
        }
    }

    private void sendUsage(CloudCommandSender sender) {
        sender.sendMessage("INFO", "§9maintenance <add> <player> §7| §bAdds player to maintenance");
        sender.sendMessage("INFO", "§9maintenance <remove> <player> §7| §bRemoves player from maintenance");
        sender.sendMessage("INFO", "§9maintenance <switch> §7| §bToggles maintenance");
        sender.sendMessage("INFO", "§9maintenance <list> §7| §bList all maintenance playerts");
    }

    @Override
    public List<String> onTabComplete(CloudDriver cloudDriver, String[] args) {
        List<String> list = new LinkedList<>();
        if (args.length == 2) {
            list.add("add");
            list.add("list");
            list.add("remove");
            list.add("switch");
        } else if (args.length == 3 && args[1].equalsIgnoreCase("remove")) {
            list.addAll(cloudDriver.getInstance(ConfigService.class).getNetworkConfig().getWhitelistedPlayers());
        }
        return list;
    }
}
