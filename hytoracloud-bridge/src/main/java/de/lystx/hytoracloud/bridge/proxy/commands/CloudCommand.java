package de.lystx.hytoracloud.bridge.proxy.commands;

import com.google.common.collect.ImmutableList;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.elements.packets.both.other.PacketTPS;
import de.lystx.hytoracloud.driver.elements.packets.in.PacketInGetLog;
import de.lystx.hytoracloud.driver.elements.packets.in.PacketShutdown;
import de.lystx.hytoracloud.driver.service.other.Updater;
import de.lystx.hytoracloud.driver.elements.packets.result.ResultPacketTPS;
import de.lystx.hytoracloud.driver.elements.packets.in.PacketInReload;
import de.lystx.hytoracloud.driver.elements.service.Service;
import de.lystx.hytoracloud.driver.elements.service.ServiceGroup;
import de.lystx.hytoracloud.driver.elements.service.ServiceType;
import de.lystx.hytoracloud.driver.elements.packets.result.ResultPacketStartService;
import de.lystx.hytoracloud.driver.service.command.base.CloudCommandSender;
import de.lystx.hytoracloud.driver.service.command.base.Command;
import de.lystx.hytoracloud.driver.service.command.command.TabCompletable;
import de.lystx.hytoracloud.driver.service.config.impl.NetworkConfig;
import de.lystx.hytoracloud.driver.service.config.impl.proxy.GlobalProxyConfig;
import de.lystx.hytoracloud.driver.service.player.impl.CloudPlayer;
import de.lystx.hytoracloud.driver.service.player.impl.PlayerInformation;
import de.lystx.hytoracloud.driver.service.util.Utils;

import java.util.List;

public class CloudCommand implements TabCompletable {

    @Command(name = "cloud", description = "Cloud Proy Command", aliases = {"hytoracloud", "hcloud", "cloudsystem", "klaud"})
    public void execute(CloudCommandSender commandSender, String[] args) {
        if (commandSender instanceof CloudPlayer) {
            CloudPlayer player = (CloudPlayer)commandSender;
            if (player.hasPermission("cloudsystem.command")) {
                if (args.length == 1) {

                    if (args[0].equalsIgnoreCase("rl") || args[0].equalsIgnoreCase("reload")) {
                        CloudDriver.getInstance().sendPacket(new PacketInReload());
                        player.sendMessage(CloudDriver.getInstance().getCloudPrefix() + "§7The CloudSystem was §areloaded§8!");

                    } else if (args[0].equalsIgnoreCase("tps")) {


                        CloudDriver.getInstance().sendPacket(new ResultPacketTPS(), response -> player.sendMessage(CloudDriver.getInstance().getCloudPrefix() + "§6TPS§8: §b" + response.getMessage()));


                    } else if (args[0].equalsIgnoreCase("version") || args[0].equalsIgnoreCase("ver")) {

                        player.sendMessage(CloudDriver.getInstance().getCloudPrefix() + "§7CloudSystem Version §a" + Updater.getCloudVersion());
                        player.sendMessage(CloudDriver.getInstance().getCloudPrefix() + "§7Your Cloud " + (Updater.isUpToDate() ? "is §aNewest version" : "§cneeds an update!"));

                    } else if (args[0].equalsIgnoreCase("shutdown")) {
                        player.sendMessage(CloudDriver.getInstance().getCloudPrefix() + "§7Cloud will be shut down in §e3 Seconds§8...");

                        Utils.doUntilEmpty(
                                CloudDriver.getInstance().getCloudPlayerManager().getOnlinePlayers(),
                                cloudPlayer -> cloudPlayer.kick(CloudDriver.getInstance().getCloudPrefix() + "§cNetwork was §eshut down!"),
                                cloudPlayers -> CloudDriver.getInstance().sendPacket(new PacketShutdown())
                        );
                    } else {
                        this.help(player);
                    }

                } else if (args.length == 2) {
                    if (args[0].equalsIgnoreCase("run")) {
                        String groupname = args[1];
                        ServiceGroup group = CloudDriver.getInstance().getServiceManager().getServiceGroup(groupname);
                        if (group == null) {
                            player.sendMessage(CloudDriver.getInstance().getCloudPrefix() + "§cThe group §e" + groupname + " §cseems not to exist!");
                            return;
                        }

                        player.sendMessage(CloudDriver.getInstance().getCloudPrefix() + "§7Trying to start a new service of group §a" + group.getName() + "§8...");


                        CloudDriver.getInstance().sendPacket(new ResultPacketStartService(groupname));

                    } else if (args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("remove")) {
                        boolean add = args[0].equalsIgnoreCase("add");
                        String playername = args[1];
                        if (add && CloudDriver.getInstance().getNetworkConfig().getNetworkConfig().getWhitelistedPlayers().contains(playername)) {
                            player.sendMessage(CloudDriver.getInstance().getCloudPrefix() + "§cThe player §e" + playername + " §cis already added to maintenance§8!");
                            return;
                        }
                        if (!add && !CloudDriver.getInstance().getNetworkConfig().getNetworkConfig().getWhitelistedPlayers().contains(playername)) {
                            player.sendMessage(CloudDriver.getInstance().getCloudPrefix() + "§cThe player §e" + playername + " §cis not added to maintenance§8!");
                            return;
                        }

                        NetworkConfig networkConfig = CloudDriver.getInstance().getNetworkConfig();
                        List<String> whitelist = networkConfig.getNetworkConfig().getWhitelistedPlayers();
                        if (add) {
                            whitelist.add(playername);
                        } else {
                            whitelist.remove(playername);
                        }
                        GlobalProxyConfig globalProxyConfig = networkConfig.getNetworkConfig();
                        globalProxyConfig.setWhitelistedPlayers(whitelist);
                        networkConfig.setNetworkConfig(globalProxyConfig);
                        networkConfig.update();

                        CloudPlayer cloudPlayer = CloudDriver.getInstance().getCloudPlayerManager().getCachedPlayer(playername);

                        if (!add) {
                            if (!CloudDriver
                                    .getInstance()
                                    .getNetworkConfig()
                                    .getNetworkConfig()
                                    .getWhitelistedPlayers()
                                    .contains(player.getName())
                                    && !player.hasPermission("cloudsystem.network.maintenance")) {
                                cloudPlayer.kick(
                                        CloudDriver.getInstance()
                                                .getNetworkConfig()
                                                .getMessageConfig()
                                                .getMaintenanceKickMessage()
                                                .replace("%prefix%",
                                                        CloudDriver.getInstance().getCloudPrefix()));
                            }
                        }
                        player.sendMessage(CloudDriver.getInstance().getCloudPrefix() + "§7The player §b" + playername + " §7was " + (add ? "§aadded to §7" : "§cremoved from §7") + "maintenance§8!");
                    } else if (args[0].equalsIgnoreCase("toggle")) {
                        if (args[1].equalsIgnoreCase("maintenance")) {
                            boolean maintenance = !CloudDriver.getInstance().getNetworkConfig().getNetworkConfig().isMaintenance();
                            NetworkConfig networkConfig = CloudDriver.getInstance().getNetworkConfig();
                            GlobalProxyConfig globalProxyConfig = networkConfig.getNetworkConfig();
                            globalProxyConfig.setMaintenance(maintenance);
                            networkConfig.setNetworkConfig(globalProxyConfig);
                            networkConfig.update();
                            player.sendMessage(CloudDriver.getInstance().getCloudPrefix() + (maintenance ? "§7The Network is now in §amaintenance§8!" : "§7The Network is §cno longer §7in maintenance§8!"));
                        } else if (args[1].equalsIgnoreCase("notify")) {
                            PlayerInformation playerData = CloudDriver.getInstance().getPermissionPool().getPlayerInformation(player.getUniqueId());
                            if (playerData == null) {
                                player.sendMessage(CloudDriver.getInstance().getCloudPrefix() + "§cYour §eCloudPlayerData §ccouldn't be found!");
                                return;
                            }
                            boolean change = !playerData.isNotifyServerStart();
                            playerData.setNotifyServerStart(change);
                            CloudDriver.getInstance().getPermissionPool().updatePlayer(playerData);
                            CloudDriver.getInstance().getPermissionPool().update();
                            player.sendMessage(CloudDriver.getInstance().getCloudPrefix() + (change ? "§7You will §anow receive §7Server notifications§8!" : "§7You will §cno longer receive §7Server notifications§8!"));
                        } else {
                            help(player);
                        }
                    } else if (args[0].equalsIgnoreCase("log")) {
                        String s = args[1];
                        Service service = CloudDriver.getInstance().getServiceManager().getService(s);
                        if (service == null) {
                            player.sendMessage(CloudDriver.getInstance().getCloudPrefix() + "§cThe service §e" + s + " §cseems not to be online!");
                            return;
                        }
                        CloudPlayer cloudPlayer = CloudDriver.getInstance().getCloudPlayerManager().getCachedPlayer(player.getName());
                        CloudDriver.getInstance().sendPacket(new PacketInGetLog(service.getName(), cloudPlayer.getName()));
                    } else if (args[0].equalsIgnoreCase("tps")) {
                        String groupname = args[1];
                        ServiceGroup group = CloudDriver.getInstance().getServiceManager().getServiceGroup(groupname);
                        if (group == null) {
                            player.sendMessage(CloudDriver.getInstance().getCloudPrefix() + "§cThe group §e" + groupname + " §cseems not to exist!");
                            return;
                        }
                        CloudPlayer cachedPlayer = CloudDriver.getInstance().getCloudPlayerManager().getCachedPlayer(player.getName());

                        cachedPlayer.sendMessage(CloudDriver.getInstance().getCloudPrefix() + "§7TPS of group §b" + group.getName() + "§8:");
                        CloudDriver.getInstance().sendPacket(new PacketTPS(cachedPlayer.getName(),CloudDriver.getInstance().getServiceManager().getAllServices().get(0), null));

                    } else if (args[0].equalsIgnoreCase("stop")) {
                        String s = args[1];
                        Service service = CloudDriver.getInstance().getServiceManager().getService(s);
                        if (service == null) {
                            player.sendMessage(CloudDriver.getInstance().getCloudPrefix() + "§cThe service §e" + s + " §cseems not to be online!");
                            return;
                        }
                        player.sendMessage(CloudDriver.getInstance().getCloudPrefix() + "§7Trying to stop service §c" + service.getName() + "§8...");
                        CloudDriver.getInstance().getServiceManager().stopService(service);
                    } else if (args[0].equalsIgnoreCase("stopGroup")) {
                        String g = args[1];
                        ServiceGroup group = CloudDriver.getInstance().getServiceManager().getServiceGroup(g);
                        if (group == null) {
                            player.sendMessage(CloudDriver.getInstance().getCloudPrefix() + "§cThe group §e" + g + " §cseems not to exist!");
                            return;
                        }
                        CloudDriver.getInstance().getServiceManager().stopServices(group);
                        player.sendMessage(CloudDriver.getInstance().getCloudPrefix() + "§7Trying to stop services from group §c" + group.getName() + "§8...");
                    } else if (args[0].equalsIgnoreCase("list")) {
                        if (args[1].equalsIgnoreCase("group")) {
                            player.sendMessage(CloudDriver.getInstance().getCloudPrefix() + "§7Groups:");

                            for (ServiceGroup serviceGroup : CloudDriver.getInstance().getServiceManager().getServiceGroups()) {
                                player.sendMessage("§8» §b" + serviceGroup.getName());
                                player.sendMessage("  §8» §b" + serviceGroup.getServiceType() + " §8| §f" + serviceGroup.getTemplate().getName() + "-Template");
                                player.sendMessage("  §8» §b" + serviceGroup.getReceiver());
                                player.sendMessage("  §8» §b" + serviceGroup.getMaxRam() + "MB");
                                player.sendMessage("  §8» §b" + serviceGroup.getNewServerPercent() + "%");
                                player.sendMessage("  §8» §b" + serviceGroup.getMaxPlayers() + " players max");
                                player.sendMessage("  §8» §b" + serviceGroup.getMaxServer() + " servers max");
                                player.sendMessage("  §8» §b" + serviceGroup.getMinServer() + " servers min");
                                player.sendMessage("§8§m---------");
                            }

                        } else if (args[1].equalsIgnoreCase("proxy")) {
                            player.sendMessage(CloudDriver.getInstance().getCloudPrefix() + "§7Proxys:");
                            for (Service service : CloudDriver.getInstance().getServiceManager().getAllServices(ServiceType.PROXY)) {
                                player.sendMessage("§8» §b" + service.getName() + " §8| §f" + service.getServiceGroup().getReceiver());
                            }
                            player.sendMessage("§8§m---------");

                        } else if (args[1].equalsIgnoreCase("server")) {
                            player.sendMessage(CloudDriver.getInstance().getCloudPrefix() + "§7Servers:");
                            for (Service service : CloudDriver.getInstance().getServiceManager().getAllServices(ServiceType.SPIGOT)) {
                                player.sendMessage("§8» §b" + service.getName() + " §8| " +
                                        service.getServiceState().getColor() + service.getServiceState().name()
                                        + " §8| §b" + CloudDriver.getInstance().getHost().getAddress().getHostAddress() + "§8:" + "§b" + service.getPort()
                                        + " §8| §b" + service.getMotd()
                                        + " §8| §b" + service.getOnlinePlayers().size() + "§8/§b" + service.getMaxPlayers()
                                );
                            }
                            player.sendMessage("§8§m---------");
                        } else if (args[1].equalsIgnoreCase("maintenance")) {
                            player.sendMessage(CloudDriver.getInstance().getCloudPrefix() + "§7Players:");
                            for (String whitelistedPlayer : CloudDriver.getInstance().getNetworkConfig().getNetworkConfig().getWhitelistedPlayers()) {
                                player.sendMessage("§8» §7" + whitelistedPlayer);
                            }
                            player.sendMessage("§8§m---------");
                        } else {
                            this.help(player);
                        }
                    } else {
                        this.help(player);
                    }
                } else if (args.length == 3) {
                    if (args[0].equalsIgnoreCase("run")) {
                        try {
                            int id = Integer.parseInt(args[2]);
                            String groupname = args[1];
                            ServiceGroup group = CloudDriver.getInstance().getServiceManager().getServiceGroup(groupname);

                            if (group == null) {
                                player.sendMessage(CloudDriver.getInstance().getCloudPrefix() + "§cThe group §e" + groupname + " §cseems not to exist!");
                                return;
                            }

                            player.sendMessage(CloudDriver.getInstance().getCloudPrefix() + "§7Trying to start §b" + id + " §7new services of group §a" + group.getName() + "§8...");

                            for (int i = 0; i < id; i++) CloudDriver.getInstance().getServiceManager().startService(group);
                        } catch (NumberFormatException e) {
                            player.sendMessage(CloudDriver.getInstance().getCloudPrefix() + "§cPlease provide a valid number!");
                        }
                    } else if (args[0].equalsIgnoreCase("toggle") && args[1].equalsIgnoreCase("maintenance")) {
                        String groupname = args[2];
                        ServiceGroup serviceGroup = CloudDriver.getInstance().getServiceManager().getServiceGroup(groupname);
                        if (serviceGroup == null) {
                            player.sendMessage(CloudDriver.getInstance().getCloudPrefix() + "§cThe group §e" + groupname + " §cdoesn't exist!");
                            return;
                        }
                        boolean maintenance = !serviceGroup.isMaintenance();
                        serviceGroup.setMaintenance(maintenance);
                        serviceGroup.update();
                        player.sendMessage(CloudDriver.getInstance().getCloudPrefix() + "§7The group §b" + serviceGroup.getName() + " §7is " + (maintenance ? "§anow in maintenance§8!" : "§cno longer in maintenance§8!"));
                    } else if (args[0].equalsIgnoreCase("copy")) {
                        String servername = args[1];
                        String templatename = args[2];
                        Service service = CloudDriver.getInstance().getServiceManager().getService(servername);
                        if (service == null) {
                            player.sendMessage(CloudDriver.getInstance().getCloudPrefix() + "§cThe service §e" + servername + " §cdoesn't exist!");
                            return;
                        }

                        CloudDriver.getInstance().copyTemplate(service, templatename);
                        player.sendMessage(CloudDriver.getInstance().getCloudPrefix() + "§7Copied §b" + servername + " §7into template §b" + templatename);
                    } else {
                        this.help(player);
                    }
                } else if (args.length == 4) {
                    if (args[0].equalsIgnoreCase("copy")) {
                        String servername = args[1];
                        String templatename = args[2];
                        String directory = args[3];
                        Service service = CloudDriver.getInstance().getServiceManager().getService(servername);
                        if (service == null) {
                            player.sendMessage(CloudDriver.getInstance().getCloudPrefix() + "§cThe service §e" + servername + " §cdoesn't exist!");
                            return;
                        }

                        CloudDriver.getInstance().copyTemplate(service, templatename, directory);
                        player.sendMessage(CloudDriver.getInstance().getCloudPrefix() + "§7Copied folder §e" + directory + " §7from Service §b" + servername + " §7into template §b" + templatename);
                    } else {
                        this.help(player);
                    }
                } else {
                    this.help(player);
                }
            } else {
                player.sendMessage(CloudDriver.getInstance().getCloudPrefix() + "§cYou aren't allowed to perform this command!");
            }
        }
    }

    public void help(CloudPlayer player) {
        player.sendMessage("§bHytoraCloud §7Help§8:");
        player.sendMessage("§8§m--------------------------------------");
        player.sendMessage("  §8» §b/cloud list <group/proxy/server/maintenance> §8┃ §7Lists network specified things");
        player.sendMessage("  §8» §b/cloud ver §8┃ §7Shows the current version");
        player.sendMessage("  §8» §b/cloud shutdown §8┃ §7Shuts down the cloud");
        player.sendMessage("  §8» §b/cloud rl §8┃ §7Reloads the cloud");
        player.sendMessage("  §8» §b/cloud toggle maintenance (group) §8┃ §7Toggles maintenance (of a group/of network)");
        player.sendMessage("  §8» §b/cloud toggle notify §8┃ §7Toggles Server notifications");
        player.sendMessage("  §8» §b/cloud add <player> §8┃ §7Adds a player to maintenance");
        player.sendMessage("  §8» §b/cloud remove <player> §8┃ §7Removes a player from maintenance");
        player.sendMessage("  §8» §b/cloud run <group> §8┃ §7Starts one server from a group");
        player.sendMessage("  §8» §b/cloud stopGroup <group> §8┃ §7Stops all servers from a group");
        player.sendMessage("  §8» §b/cloud copy <server> <template> (specific directory) §8┃ §7Copies a server into a template");
        player.sendMessage("  §8» §b/cloud stop <server> §8┃ §7Stops a specific server or proxy");
        player.sendMessage("  §8» §b/cloud log <server> §8┃ §7Logs a servers output");
        player.sendMessage("  §8» §b/cloud tps <group> §8┃ §7Shows tps of all servers of group ");
        player.sendMessage("  §8» §b/cloud tps §8┃ §7Shows tps of cloudsystem");
        player.sendMessage("§8§m--------------------------------------");
    }


    @Override
    public List<String> onTabComplete(CloudDriver cloudDriver, String[] args) {
        if (args[0].equalsIgnoreCase("list")) {
            return ImmutableList.of("group", "proxy", "server", "maintenance");
        } else if (args[0].equalsIgnoreCase("stop") || args[0].equalsIgnoreCase("log")) {
            return Utils.toStringList(CloudDriver.getInstance().getServiceManager().getAllServices());
        } else if (args[0].equalsIgnoreCase("stopGroup") || args[0].equalsIgnoreCase("run") || args[0].equalsIgnoreCase("tps")) {
            return Utils.toStringList(CloudDriver.getInstance().getServiceManager().getServiceGroups());
        } else if (args[0].equalsIgnoreCase("maintenance")) {
            if (args.length == 2) {
                List<String> groups = Utils.toStringList(CloudDriver.getInstance().getServiceManager().getServiceGroups());
                groups.add("switch");
                return groups;
            } else if (args.length == 3) {
                return ImmutableList.of("true", "false");
            }
        }
        return ImmutableList.of("list", "tps", "ver", "shutdown", "rl", "maintenance", "run", "stop", "stopGroup", "copy", "log", "toggle", "stats");
    }
}
