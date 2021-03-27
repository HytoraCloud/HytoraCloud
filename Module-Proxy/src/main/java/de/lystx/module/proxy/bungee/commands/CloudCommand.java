package de.lystx.module.proxy.bungee.commands;

import com.google.common.collect.ImmutableList;
import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.elements.packets.result.Result;
import de.lystx.cloudsystem.library.service.updater.Updater;
import de.lystx.cloudsystem.library.elements.packets.result.login.ResultPacketTPS;
import de.lystx.cloudsystem.library.elements.packets.in.other.PacketInReload;
import de.lystx.cloudsystem.library.elements.service.Service;
import de.lystx.cloudsystem.library.elements.service.ServiceGroup;
import de.lystx.cloudsystem.library.elements.service.ServiceType;
import de.lystx.cloudsystem.library.elements.packets.result.services.ResultPacketStartService;
import de.lystx.cloudsystem.library.service.command.base.CloudCommandSender;
import de.lystx.cloudsystem.library.service.command.base.Command;
import de.lystx.cloudsystem.library.service.command.command.TabCompletable;
import de.lystx.cloudsystem.library.service.config.impl.NetworkConfig;
import de.lystx.cloudsystem.library.service.config.impl.proxy.GlobalProxyConfig;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayer;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayerData;
import de.lystx.cloudsystem.library.service.util.Utils;
import de.lystx.cloudsystem.library.service.util.Value;
import io.vson.elements.object.VsonObject;
import net.md_5.bungee.api.ProxyServer;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.IntStream;

public class CloudCommand implements TabCompletable {

    @Command(name = "cloud", description = "Cloud Proy Command", aliases = {"hytoracloud", "hcloud", "cloudsystem", "klaud"})
    public void execute(CloudCommandSender commandSender, String[] args) {
        if (commandSender instanceof CloudPlayer) {
            CloudPlayer player = (CloudPlayer)commandSender;
            if (player.hasPermission("cloudsystem.command")) {
                if (args.length == 1) {

                    if (args[0].equalsIgnoreCase("rl") || args[0].equalsIgnoreCase("reload")) {
                        CloudAPI.getInstance().getCloudClient().sendPacket(new PacketInReload());
                        player.sendMessage(CloudAPI.getInstance().getPrefix() + "§7The CloudSystem was §areloaded§8!");

                    } else if (args[0].equalsIgnoreCase("tps")) {

                        String format = CloudAPI.getInstance().sendQuery(new ResultPacketTPS()).getResult();
                        player.sendMessage(CloudAPI.getInstance().getPrefix() + "§6TPS§8: §b" + format);

                    } else if (args[0].equalsIgnoreCase("version") || args[0].equalsIgnoreCase("ver")) {

                        player.sendMessage(CloudAPI.getInstance().getPrefix() + "§7CloudSystem Version §a" + Updater.getCloudVersion());
                        player.sendMessage(CloudAPI.getInstance().getPrefix() + "§7Your Cloud " + (Updater.isUpToDate() ? "is §aNewest version" : "§cneeds an update!"));

                    } else if (args[0].equalsIgnoreCase("shutdown")) {
                        player.sendMessage(CloudAPI.getInstance().getPrefix() + "§7Cloud will be shut down in §e3 Seconds§8...");
                        CloudAPI.getInstance().setJoinable(false);

                        Utils.doUntilEmpty(
                                CloudAPI.getInstance().getCloudPlayers().getAll(),
                                cloudPlayer -> cloudPlayer.kick(CloudAPI.getInstance().getPrefix() + "§cNetwork was §eshut down!"),
                                cloudPlayers -> CloudAPI.getInstance().getNetwork().shutdownCloud()
                        );
                    } else {
                        this.help(player);
                    }

                } else if (args.length == 2) {
                    if (args[0].equalsIgnoreCase("run")) {
                        String groupname = args[1];
                        ServiceGroup group = CloudAPI.getInstance().getNetwork().getServiceGroup(groupname);
                        if (group == null) {
                            player.sendMessage(CloudAPI.getInstance().getPrefix() + "§cThe group §e" + groupname + " §cseems not to exist!");
                            return;
                        }

                        player.sendMessage(CloudAPI.getInstance().getPrefix() + "§7Trying to start a new service of group §a" + group.getName() + "§8...");
                        try {
                            CloudAPI.getInstance().sendQuery(new ResultPacketStartService(groupname), vsonObjectResult -> {
                                VsonObject document = vsonObjectResult.getResult();
                                String message = document.getString("message");
                                if (!document.getBoolean("sucess", false)) {
                                    player.sendMessage(CloudAPI.getInstance().getPrefix() + message);
                                }
                            });
                        } catch (NullPointerException e) {
                            //Ignoring everything went well
                        }
                    } else if (args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("remove")) {
                        boolean add = args[0].equalsIgnoreCase("add");
                        String playername = args[1];
                        if (add && CloudAPI.getInstance().getNetworkConfig().getNetworkConfig().getWhitelistedPlayers().contains(playername)) {
                            player.sendMessage(CloudAPI.getInstance().getPrefix() + "§cThe player §e" + playername + " §cis already added to maintenance§8!");
                            return;
                        }
                        if (!add && !CloudAPI.getInstance().getNetworkConfig().getNetworkConfig().getWhitelistedPlayers().contains(playername)) {
                            player.sendMessage(CloudAPI.getInstance().getPrefix() + "§cThe player §e" + playername + " §cis not added to maintenance§8!");
                            return;
                        }

                        NetworkConfig networkConfig = CloudAPI.getInstance().getNetworkConfig();
                        List<String> whitelist = networkConfig.getNetworkConfig().getWhitelistedPlayers();
                        if (add) {
                            whitelist.add(playername);
                        } else {
                            whitelist.remove(playername);
                        }
                        GlobalProxyConfig globalProxyConfig = networkConfig.getNetworkConfig();
                        globalProxyConfig.setWhitelistedPlayers(whitelist);
                        networkConfig.setNetworkConfig(globalProxyConfig);
                        CloudAPI.getInstance().getNetwork().updateNetworkConfig(networkConfig);
                        player.sendMessage(CloudAPI.getInstance().getPrefix() + "§7The player §b" + playername + " §7was " + (add ? "§aadded to §7" : "§cremoved from §7") + "maintenance§8!");
                    } else if (args[0].equalsIgnoreCase("toggle")) {
                        if (args[1].equalsIgnoreCase("maintenance")) {
                            boolean maintenance = !CloudAPI.getInstance().getNetworkConfig().getNetworkConfig().isMaintenance();
                            NetworkConfig networkConfig = CloudAPI.getInstance().getNetworkConfig();
                            GlobalProxyConfig globalProxyConfig = networkConfig.getNetworkConfig();
                            globalProxyConfig.setMaintenance(maintenance);
                            networkConfig.setNetworkConfig(globalProxyConfig);
                            CloudAPI.getInstance().getNetwork().updateNetworkConfig(networkConfig);
                            player.sendMessage(CloudAPI.getInstance().getPrefix() + (maintenance ? "§7The Network is now in §amaintenance§8!" : "§7The Network is §cno longer §7in maintenance§8!"));
                        } else if (args[1].equalsIgnoreCase("notify")) {
                            CloudPlayerData playerData = CloudAPI.getInstance().getPermissionPool().getPlayerData(player.getName());
                            if (playerData == null) {
                                player.sendMessage(CloudAPI.getInstance().getPrefix() + "§cYour §eCloudPlayerData §ccouldn't be found!");
                                return;
                            }
                            boolean change = !playerData.isNotifyServerStart();
                            playerData.setNotifyServerStart(change);
                            CloudAPI.getInstance().getPermissionPool().updatePlayerData(player.getName(), playerData);
                            CloudAPI.getInstance().getPermissionPool().update();
                            player.sendMessage(CloudAPI.getInstance().getPrefix() + (change ? "§7You will §anow receive §7Server notifications§8!" : "§7You will §cno longer receive §7Server notifications§8!"));
                        } else {
                            help(player);
                        }
                    } else if (args[0].equalsIgnoreCase("log")) {
                        String s = args[1];
                        Service service = CloudAPI.getInstance().getNetwork().getService(s);
                        if (service == null) {
                            player.sendMessage(CloudAPI.getInstance().getPrefix() + "§cThe service §e" + s + " §cseems not to be online!");
                            return;
                        }
                        CloudPlayer cloudPlayer = CloudAPI.getInstance().getCloudPlayers().get(player.getName());
                        CloudAPI.getInstance().getCloudPlayers().sendLog(cloudPlayer, service);
                    } else if (args[0].equalsIgnoreCase("tps")) {
                        String groupname = args[1];
                        ServiceGroup group = CloudAPI.getInstance().getNetwork().getServiceGroup(groupname);
                        if (group == null) {
                            player.sendMessage(CloudAPI.getInstance().getPrefix() + "§cThe group §e" + groupname + " §cseems not to exist!");
                            return;
                        }
                        CloudAPI.getInstance().getNetwork().sendTPS(group, CloudAPI.getInstance().getCloudPlayers().get(player.getName()));
                    } else if (args[0].equalsIgnoreCase("stop")) {
                        String s = args[1];
                        Service service = CloudAPI.getInstance().getNetwork().getService(s);
                        if (service == null) {
                            player.sendMessage(CloudAPI.getInstance().getPrefix() + "§cThe service §e" + s + " §cseems not to be online!");
                            return;
                        }
                        player.sendMessage(CloudAPI.getInstance().getPrefix() + "§7Trying to stop service §c" + service.getName() + "§8...");
                        CloudAPI.getInstance().getNetwork().stopService(service);
                    } else if (args[0].equalsIgnoreCase("stopGroup")) {
                        String g = args[1];
                        ServiceGroup group = CloudAPI.getInstance().getNetwork().getServiceGroup(g);
                        if (group == null) {
                            player.sendMessage(CloudAPI.getInstance().getPrefix() + "§cThe group §e" + g + " §cseems not to exist!");
                            return;
                        }
                        CloudAPI.getInstance().getNetwork().stopServices(group);
                        player.sendMessage(CloudAPI.getInstance().getPrefix() + "§7Trying to stop services from group §c" + group.getName() + "§8...");
                    } else if (args[0].equalsIgnoreCase("list")) {
                        if (args[1].equalsIgnoreCase("group")) {
                            player.sendMessage(CloudAPI.getInstance().getPrefix() + "§7Groups:");

                            for (ServiceGroup serviceGroup : CloudAPI.getInstance().getNetwork().getServiceGroups()) {
                                player.sendMessage("§8» §b" + serviceGroup.getName());
                                player.sendMessage("  §8» §b" + serviceGroup.getServiceType() + " §8| §f" + serviceGroup.getTemplate() + "-Template");
                                player.sendMessage("  §8» §b" + serviceGroup.getReceiver());
                                player.sendMessage("  §8» §b" + serviceGroup.getMaxRam() + "MB");
                                player.sendMessage("  §8» §b" + serviceGroup.getNewServerPercent() + "%");
                                player.sendMessage("  §8» §b" + serviceGroup.getMaxPlayers() + " players max");
                                player.sendMessage("  §8» §b" + serviceGroup.getMaxServer() + " servers max");
                                player.sendMessage("  §8» §b" + serviceGroup.getMinServer() + " servers min");
                                player.sendMessage("§8§m---------");
                            }

                        } else if (args[1].equalsIgnoreCase("proxy")) {
                            player.sendMessage(CloudAPI.getInstance().getPrefix() + "§7Proxys:");
                            for (Service service : CloudAPI.getInstance().getNetwork().getServices(ServiceType.PROXY)) {
                                player.sendMessage("§8» §b" + service.getName() + " §8| §f" + service.getServiceGroup().getReceiver());
                            }
                            player.sendMessage("§8§m---------");

                        } else if (args[1].equalsIgnoreCase("server")) {
                            player.sendMessage(CloudAPI.getInstance().getPrefix() + "§7Servers:");
                            for (Service service : CloudAPI.getInstance().getNetwork().getServices(ServiceType.SPIGOT)) {
                                player.sendMessage("§8» §b" + service.getName() + " §8| " +
                                        service.getServiceState().getColor() + service.getServiceState().name()
                                        + " §8| §b" + service.getHost() + "§8:" + "§b" + service.getPort()
                                        + " §8| §b" + service.getInfo().getMotd()
                                        + " §8| §b" + service.getInfo().getOnlinePlayers().size() + "§8/§b" + service.getInfo().getMaxPlayers()
                                );
                            }
                            player.sendMessage("§8§m---------");
                        } else if (args[1].equalsIgnoreCase("maintenance")) {
                            player.sendMessage(CloudAPI.getInstance().getPrefix() + "§7Players:");
                            for (String whitelistedPlayer : CloudAPI.getInstance().getNetworkConfig().getNetworkConfig().getWhitelistedPlayers()) {
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
                            ServiceGroup group = CloudAPI.getInstance().getNetwork().getServiceGroup(groupname);

                            if (group == null) {
                                player.sendMessage(CloudAPI.getInstance().getPrefix() + "§cThe group §e" + groupname + " §cseems not to exist!");
                                return;
                            }

                            player.sendMessage(CloudAPI.getInstance().getPrefix() + "§7Trying to start §b" + id + " §7new services of group §a" + group.getName() + "§8...");

                            for (int i = 0; i < id; i++) CloudAPI.getInstance().getNetwork().startService(group);
                        } catch (NumberFormatException e) {
                            player.sendMessage(CloudAPI.getInstance().getPrefix() + "§cPlease provide a valid number!");
                        }
                    } else if (args[0].equalsIgnoreCase("toggle") && args[1].equalsIgnoreCase("maintenance")) {
                        String groupname = args[2];
                        ServiceGroup serviceGroup = CloudAPI.getInstance().getNetwork().getServiceGroup(groupname);
                        if (serviceGroup == null) {
                            player.sendMessage(CloudAPI.getInstance().getPrefix() + "§cThe group §e" + groupname + " §cdoesn't exist!");
                            return;
                        }
                        boolean maintenance = !serviceGroup.isMaintenance();
                        serviceGroup.setMaintenance(maintenance);
                        serviceGroup.update();
                        player.sendMessage(CloudAPI.getInstance().getPrefix() + "§7The group §b" + serviceGroup.getName() + " §7is " + (maintenance ? "§anow in maintenance§8!" : "§cno longer in maintenance§8!"));
                    } else if (args[0].equalsIgnoreCase("copy")) {
                        String servername = args[1];
                        String templatename = args[2];
                        Service service = CloudAPI.getInstance().getNetwork().getService(servername);
                        if (service == null) {
                            player.sendMessage(CloudAPI.getInstance().getPrefix() + "§cThe service §e" + servername + " §cdoesn't exist!");
                            return;
                        }

                        CloudAPI.getInstance().getTemplates().copy(servername, templatename);
                        player.sendMessage(CloudAPI.getInstance().getPrefix() + "§7Copied §b" + servername + " §7into template §b" + templatename);
                    } else {
                        this.help(player);
                    }
                } else if (args.length == 4) {
                    if (args[0].equalsIgnoreCase("copy")) {
                        String servername = args[1];
                        String templatename = args[2];
                        String directory = args[3];
                        Service service = CloudAPI.getInstance().getNetwork().getService(servername);
                        if (service == null) {
                            player.sendMessage(CloudAPI.getInstance().getPrefix() + "§cThe service §e" + servername + " §cdoesn't exist!");
                            return;
                        }

                        CloudAPI.getInstance().getTemplates().copy(servername, templatename, directory);
                        player.sendMessage(CloudAPI.getInstance().getPrefix() + "§7Copied folder §e" + directory + " §7from Service §b" + servername + " §7into template §b" + templatename);
                    } else {
                        this.help(player);
                    }
                } else {
                    this.help(player);
                }
            } else {
                player.sendMessage(CloudAPI.getInstance().getPrefix() + "§cYou aren't allowed to perform this command!");
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
    public List<String> onTabComplete(CloudLibrary cloudLibrary, String[] args) {
        if (args[0].equalsIgnoreCase("list")) {
            return ImmutableList.of("group", "proxy", "server", "maintenance");
        } else if (args[0].equalsIgnoreCase("stop") || args[0].equalsIgnoreCase("log")) {
            return Utils.toStringList(CloudAPI.getInstance().getNetwork().getServices());
        } else if (args[0].equalsIgnoreCase("stopGroup") || args[0].equalsIgnoreCase("run") || args[0].equalsIgnoreCase("tps")) {
            return Utils.toStringList(CloudAPI.getInstance().getNetwork().getServiceGroups());
        } else if (args[0].equalsIgnoreCase("maintenance")) {
            if (args.length == 2) {
                List<String> groups = Utils.toStringList(CloudAPI.getInstance().getNetwork().getServiceGroups());
                groups.add("switch");
                return groups;
            } else if (args.length == 3) {
                return ImmutableList.of("true", "false");
            }
        }
        return ImmutableList.of("list", "tps", "ver", "shutdown", "rl", "maintenance", "run", "stop", "stopGroup", "copy", "log", "toggle", "stats");
    }
}
