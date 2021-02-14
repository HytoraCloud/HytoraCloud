package de.lystx.cloudapi.proxy.command;

import com.google.common.collect.ImmutableList;
import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudsystem.library.elements.packets.in.other.PacketPlayInTPS;
import de.lystx.cloudsystem.library.elements.packets.in.other.PacketPlayInReload;
import de.lystx.cloudsystem.library.elements.service.Service;
import de.lystx.cloudsystem.library.elements.service.ServiceGroup;
import de.lystx.cloudsystem.library.elements.service.ServiceType;
import de.lystx.cloudsystem.library.result.packets.services.ResultPacketStartService;
import de.lystx.cloudsystem.library.service.config.impl.NetworkConfig;
import de.lystx.cloudsystem.library.service.config.impl.proxy.ProxyConfig;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayer;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayerData;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.LinkedList;
import java.util.List;

public class CloudCommand extends Command implements TabExecutor {


    public CloudCommand() {
        super("cloud", null, "hytoracloud", "hcloud", "cloudsystem", "klaud");
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if (commandSender instanceof ProxiedPlayer) {
            ProxiedPlayer player = (ProxiedPlayer)commandSender;
            if (player.hasPermission("cloudsystem.command")) {
                if (args.length == 1) {
                    if (args[0].equalsIgnoreCase("rl") || args[0].equalsIgnoreCase("reload")) {
                        CloudAPI.getInstance().getCloudClient().sendPacket(new PacketPlayInReload());
                        player.sendMessage(CloudAPI.getInstance().getPrefix() + "§7The CloudSystem was §areloaded§8!");
                    } else if (args[0].equalsIgnoreCase("tps")) {
                        CloudAPI.getInstance().sendPacket(new PacketPlayInTPS(player.getName()));
                    } else if (args[0].equalsIgnoreCase("stats")) {
                        player.sendMessage(CloudAPI.getInstance().getPrefix() + "§bStatistics§8:");
                        CloudAPI.getInstance().getStatistics().getStats().forEach((stats, i) -> {
                            player.sendMessage(" §8» §b" + stats + " §8┃ §7" + i);
                        });
                    } else if (args[0].equalsIgnoreCase("toggle")) {
                        CloudPlayerData playerData = CloudAPI.getInstance().getPermissionPool().getPlayerData(player.getName());
                        if (playerData == null) {
                            player.sendMessage(CloudAPI.getInstance().getPrefix() + "§cYour §eCloudPlayerData §ccouldn't be found!");
                            return;
                        }
                        boolean change = !playerData.isNotifyServerStart();
                        playerData.setNotifyServerStart(change);
                        CloudAPI.getInstance().getPermissionPool().updatePlayerData(player.getName(), playerData);
                        CloudAPI.getInstance().getPermissionPool().update(CloudAPI.getInstance().getCloudClient());
                        player.sendMessage(CloudAPI.getInstance().getPrefix() + (change ? "§7You will §anow receive §7Server notifications§8!" : "§7You will §cno longer receive §7Server notifications§8!"));
                    } else if (args[0].equalsIgnoreCase("version") || args[0].equalsIgnoreCase("ver")) {
                        player.sendMessage(CloudAPI.getInstance().getPrefix() + "§7CloudSystem Version §a1.0");
                    } else if (args[0].equalsIgnoreCase("shutdown")) {
                        player.sendMessage(CloudAPI.getInstance().getPrefix() + "§7Cloud will be shut down in §e3 Seconds§8...");
                        CloudAPI.getInstance().setJoinable(false);
                        for (ProxiedPlayer proxiedPlayer : ProxyServer.getInstance().getPlayers()) {
                            proxiedPlayer.disconnect(new TextComponent(CloudAPI.getInstance().getPrefix() + "§cNetwork was §eshut down!"));
                        }
                        CloudAPI.getInstance().getNetwork().shutdownCloud();
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
                        CloudAPI.getInstance().sendQuery(new ResultPacketStartService(groupname)).onDocumentSet(document -> {
                            String message = document.getString("message");
                            if (!document.getBoolean("sucess", false)) {
                                player.sendMessage(CloudAPI.getInstance().getPrefix() + message);
                            }
                        });

                       // CloudAPI.getInstance().getNetwork().startService(group);

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
                        if (service == null || ProxyServer.getInstance().getServerInfo(s) == null) {
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
                            CloudAPI.getInstance().getNetwork().getServiceGroups().forEach(groups -> player.sendMessage(CloudAPI.getInstance().getPrefix() + "§8» §b" + groups.getName() + " §8| §bTemplate§8: §7" + groups.getTemplate()));
                        } else if (args[1].equalsIgnoreCase("proxy")) {
                            player.sendMessage(CloudAPI.getInstance().getPrefix() + "§7Proxys:");
                            CloudAPI.getInstance().getNetwork().getServices().forEach(service -> {
                                if (service.getServiceGroup().getServiceType().equals(ServiceType.PROXY)) {
                                    player.sendMessage(CloudAPI.getInstance().getPrefix() + "§8» §b" + service.getName() + " §8| §bUUID§8: §7" + service.getUniqueId());
                                }
                            });
                        } else if (args[1].equalsIgnoreCase("server")) {
                            player.sendMessage(CloudAPI.getInstance().getPrefix() + "§7Servers:");
                            CloudAPI.getInstance().getNetwork().getServices().forEach(service -> {
                                if (service.getServiceGroup().getServiceType().equals(ServiceType.SPIGOT)) {
                                    player.sendMessage(CloudAPI.getInstance().getPrefix() + "§8» §b" + service.getName() + " §8| §7" + service.getServiceState().getColor() + service.getServiceState().name());
                                }
                            });
                        } else if (args[1].equalsIgnoreCase("maintenance")) {
                            player.sendMessage(CloudAPI.getInstance().getPrefix() + "§7Players:");
                            CloudAPI.getInstance().getNetworkConfig().getProxyConfig().getWhitelistedPlayers().forEach(whitelisted -> {
                                player.sendMessage(CloudAPI.getInstance().getPrefix() + "§8» §7" + whitelisted);
                            });
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
                            for (int i = 0; i < id; i++) {
                                CloudAPI.getInstance().sendQuery(new ResultPacketStartService(groupname)).onDocumentSet(document -> {
                                    String message = document.getString("message");
                                    if (!document.getBoolean("sucess", false)) {
                                        player.sendMessage(CloudAPI.getInstance().getPrefix() + message);
                                    }
                                });
                            }
                        } catch (NumberFormatException e) {
                            player.sendMessage(CloudAPI.getInstance().getPrefix() + "§cPlease provide a valid number!");
                        }
                    } else if (args[0].equalsIgnoreCase("maintenance")) {
                        String groupname = args[1];
                        if (groupname.equalsIgnoreCase("switch")) {
                            boolean maintenance = Boolean.parseBoolean(args[2]);
                            NetworkConfig networkConfig = CloudAPI.getInstance().getNetworkConfig();
                            ProxyConfig proxyConfig = networkConfig.getProxyConfig();
                            proxyConfig.setMaintenance(maintenance);
                            networkConfig.setProxyConfig(proxyConfig);
                            CloudAPI.getInstance().getNetwork().updateNetworkConfig(networkConfig);
                            player.sendMessage(CloudAPI.getInstance().getPrefix() + "§7Set maintenance of §bCloudSystem §7to §b" + (maintenance ? "§a" : "§c") + maintenance + "§8!");
                        } else if (groupname.equalsIgnoreCase("add")) {
                            String playername = args[2];
                            if (CloudAPI.getInstance().getNetworkConfig().getProxyConfig().getWhitelistedPlayers().contains(playername)) {
                                player.sendMessage(CloudAPI.getInstance().getPrefix() + "§cThe player §e" + playername + " §cis already added to maintenance§8!");
                                return;
                            }

                            NetworkConfig networkConfig = CloudAPI.getInstance().getNetworkConfig();
                            ProxyConfig proxyConfig = networkConfig.getProxyConfig();
                            List<String> whitelist = proxyConfig.getWhitelistedPlayers();
                            whitelist.add(playername);
                            networkConfig.setProxyConfig(proxyConfig);
                            CloudAPI.getInstance().getNetwork().updateNetworkConfig(networkConfig);
                            player.sendMessage(CloudAPI.getInstance().getPrefix() + "§7The player §b" + playername + " §7was added to maintenance§8!");
                        } else if (groupname.equalsIgnoreCase("remove")) {
                            String playername = args[2];
                            if (!CloudAPI.getInstance().getNetworkConfig().getProxyConfig().getWhitelistedPlayers().contains(playername)) {
                                player.sendMessage(CloudAPI.getInstance().getPrefix() + "§cThe player §e" + playername + " §cis not added to maintenance§8!");
                                return;
                            }
                            NetworkConfig networkConfig = CloudAPI.getInstance().getNetworkConfig();
                            ProxyConfig proxyConfig = networkConfig.getProxyConfig();
                            List<String> whitelist = proxyConfig.getWhitelistedPlayers();
                            whitelist.remove(playername);
                            networkConfig.setProxyConfig(proxyConfig);
                            CloudAPI.getInstance().getNetwork().updateNetworkConfig(networkConfig);
                            player.sendMessage(CloudAPI.getInstance().getPrefix() + "§7The player §b" + playername + " §7was removed from maintenance§8!");
                        } else {
                            boolean maintenance = Boolean.parseBoolean(args[2]);
                            if (CloudAPI.getInstance().getNetwork().getServiceGroup(groupname) == null) {
                                player.sendMessage(CloudAPI.getInstance().getPrefix() + "§cThe group §e" + groupname + " §cdoesn't exist!");
                                return;
                            }
                            ServiceGroup serviceGroup = CloudAPI.getInstance().getNetwork().getServiceGroup(groupname);
                            serviceGroup.setMaintenance(maintenance);
                            CloudAPI.getInstance().getNetwork().updateServiceGroup(serviceGroup);
                            player.sendMessage(CloudAPI.getInstance().getPrefix() + "§7Set maintenance of group §b" + groupname + " §7to §b" + (maintenance ? "§a" : "§c") + maintenance + "§8!");
                        }

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
                } else {
                    this.help(player);
                }
            } else {
                player.sendMessage(CloudAPI.getInstance().getPrefix() + "§cYou aren't allowed to perform this command!");
            }
        }
    }

    public void help(ProxiedPlayer player) {
        player.sendMessage("§bHytoraCloud §7Help§8:");
        player.sendMessage("§8§m--------------------------------------");
        player.sendMessage("  §8» §b/cloud list <group/proxy/server/maintenance> §8┃ §7Lists network specified things");
        player.sendMessage("  §8» §b/cloud ver §8┃ §7Shows the current version");
        player.sendMessage("  §8» §b/cloud shutdown §8┃ §7Shuts down the cloud");
        player.sendMessage("  §8» §b/cloud rl §8┃ §7Reloads the cloud");
        player.sendMessage("  §8» §b/cloud maintenance <group/switch> <true/false> §8┃ §7Toggles maintenance (of a group/of network)");
        player.sendMessage("  §8» §b/cloud maintenance add <player> §8┃ §7Adds a player to maintenance");
        player.sendMessage("  §8» §b/cloud maintenance remove <player> §8┃ §7Removes a player from maintenance");
        player.sendMessage("  §8» §b/cloud run <group> §8┃ §7Starts one server from a group");
        player.sendMessage("  §8» §b/cloud stopGroup <group> §8┃ §7Stops all servers from a group");
        player.sendMessage("  §8» §b/cloud copy <server> <template> §8┃ §7Copies a server into a template");
        player.sendMessage("  §8» §b/cloud stop <server> §8┃ §7Stops a specific server or proxy");
        player.sendMessage("  §8» §b/cloud log <server> §8┃ §7Logs a servers output");
        player.sendMessage("  §8» §b/cloud toggle §8┃ §7Toggles Server notifications");
        player.sendMessage("  §8» §b/cloud tps <group> §8┃ §7Shows tps of all servers of group ");
        player.sendMessage("  §8» §b/cloud tps §8┃ §7Shows tps of cloudsystem");
        player.sendMessage("  §8» §b/cloud stats §8┃ §7Shows stats of cloudsystem");
        player.sendMessage("§8§m--------------------------------------");
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender commandSender, String[] args) {
        if (args[0].equalsIgnoreCase("list")) {
            return ImmutableList.of("group", "proxy", "server", "maintenance");
        } else if (args[0].equalsIgnoreCase("stop") || args[0].equalsIgnoreCase("log")) {
            return this.getServices();
        } else if (args[0].equalsIgnoreCase("stopGroup") || args[0].equalsIgnoreCase("run") || args[0].equalsIgnoreCase("tps")) {
            return this.getGroups();
        } else if (args[0].equalsIgnoreCase("maintenance")) {
            if (args.length == 2) {
                List<String> groups = this.getGroups();
                groups.add("switch");
                return groups;
            } else if (args.length == 3) {
                return ImmutableList.of("true", "false");
            }
        }
        return ImmutableList.of("list", "tps", "ver", "shutdown", "rl", "maintenance", "run", "stop", "stopGroup", "copy", "log", "toggle", "stats");
    }

    public List<String> getServices() {
        List<String> list = new LinkedList<>();
        for (Service service : CloudAPI.getInstance().getNetwork().getServices()) {
            list.add(service.getName());
        }
        return list;
    }

    public List<String> getGroups() {
        List<String> list = new LinkedList<>();
        for (ServiceGroup service : CloudAPI.getInstance().getNetwork().getServiceGroups()) {
            list.add(service.getName());
        }
        return list;
    }
}
