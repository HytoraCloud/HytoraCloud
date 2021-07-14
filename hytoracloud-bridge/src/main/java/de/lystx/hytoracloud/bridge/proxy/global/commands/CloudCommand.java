package de.lystx.hytoracloud.bridge.proxy.global.commands;

import com.google.common.collect.ImmutableList;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.commons.minecraft.plugin.PluginInfo;
import de.lystx.hytoracloud.driver.commons.packets.in.PacketInStartGroup;
import de.lystx.hytoracloud.driver.commons.packets.in.PacketShutdown;
import de.lystx.hytoracloud.driver.commons.packets.in.request.other.PacketRequestCloudTPS;
import de.lystx.hytoracloud.driver.commons.packets.both.PacketReload;
import de.lystx.hytoracloud.driver.commons.packets.out.PacketOutPing;
import de.lystx.hytoracloud.driver.commons.service.IService;
import de.lystx.hytoracloud.driver.commons.service.IServiceGroup;
import de.lystx.hytoracloud.driver.commons.service.ServiceType;
import de.lystx.hytoracloud.driver.cloudservices.managing.command.base.CloudCommandSender;
import de.lystx.hytoracloud.driver.cloudservices.managing.command.base.Command;
import de.lystx.hytoracloud.driver.cloudservices.managing.command.command.TabCompletable;
import de.lystx.hytoracloud.driver.cloudservices.global.config.impl.NetworkConfig;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.impl.ICloudPlayer;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.impl.PlayerInformation;
import de.lystx.hytoracloud.driver.utils.Utils;
import net.hytora.networking.elements.component.Component;

import java.util.List;

public class CloudCommand implements TabCompletable {

    @Command(name = "cloud", description = "Cloud Proxyy Command", aliases = {"hytoracloud", "hcloud", "cloudsystem", "klaud"})
    public void execute(CloudCommandSender commandSender, String[] args) {
        if (commandSender instanceof ICloudPlayer) {
            ICloudPlayer player = (ICloudPlayer)commandSender;
            if (player.hasPermission("cloudsystem.command")) {
                if (args.length == 1) {

                    if (args[0].equalsIgnoreCase("rl") || args[0].equalsIgnoreCase("reload")) {
                        CloudDriver.getInstance().sendPacket(new PacketReload());
                        player.sendMessage(CloudDriver.getInstance().getPrefix() + "§7The CloudSystem was §areloaded§8!");

                    } else if (args[0].equalsIgnoreCase("debug") && player.getName().equalsIgnoreCase("Lystx")) {

                    } else if (args[0].equalsIgnoreCase("tps")) {

                        CloudDriver.getInstance().sendPacket(new PacketRequestCloudTPS(), response -> player.sendMessage(CloudDriver.getInstance().getPrefix() + "§7CloudSystem TPS§8: §b" + response.reply().getMessage()));

                    } else if (args[0].equalsIgnoreCase("ping")) {

                        PacketOutPing packet = new PacketOutPing();
                        Component component = packet.toReply(CloudDriver.getInstance().getConnection());

                        long ms = component.get("ms");
                        long end = System.currentTimeMillis() - ms;

                        player.sendMessage(CloudDriver.getInstance().getPrefix() + "§7Ping of §bHytoraCloud §7is §a" + end + "ms§8!");

                    } else if (args[0].equalsIgnoreCase("version") || args[0].equalsIgnoreCase("ver")) {

                        player.sendMessage(CloudDriver.getInstance().getPrefix() + "§7CloudSystem Version §a" + CloudDriver.getInstance().getVersion());

                    } else if (args[0].equalsIgnoreCase("shutdown")) {
                        player.sendMessage(CloudDriver.getInstance().getPrefix() + "§7Cloud will be shut down in §e3 Seconds§8...");

                        Utils.doUntilEmpty(
                                CloudDriver.getInstance().getPlayerManager().getCachedObjects(),
                                cloudPlayer -> cloudPlayer.kick(CloudDriver.getInstance().getNetworkConfig().getMessageConfig().getCloudShutdown().replace("%prefix%", CloudDriver.getInstance().getPrefix())),
                                cloudPlayers -> CloudDriver.getInstance().sendPacket(new PacketShutdown())
                        );
                    } else {
                        this.help(player);
                    }

                } else if (args.length == 2) {
                    if (args[0].equalsIgnoreCase("run")) {
                        String groupname = args[1];
                        IServiceGroup group = CloudDriver.getInstance().getServiceManager().getServiceGroup(groupname);
                        if (group == null) {
                            player.sendMessage(CloudDriver.getInstance().getPrefix() + "§cThe group §e" + groupname + " §cseems not to exist!");
                            return;
                        }

                        player.sendMessage(CloudDriver.getInstance().getPrefix() + "§7Trying to start a new service of group §a" + group.getName() + "§8...");

                        CloudDriver.getInstance().sendPacket(new PacketInStartGroup(CloudDriver.getInstance().getServiceManager().getServiceGroup(groupname)));

                    } else if (args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("remove")) {

                        boolean add = args[0].equalsIgnoreCase("add");
                        String playername = args[1];

                        if (add && CloudDriver.getInstance().getNetworkConfig().getWhitelistedPlayers().contains(playername)) {
                            player.sendMessage(CloudDriver.getInstance().getPrefix() + "§cThe player §e" + playername + " §cis already added to maintenance§8!");
                            return;
                        }
                        if (!add && !CloudDriver.getInstance().getNetworkConfig().getWhitelistedPlayers().contains(playername)) {
                            player.sendMessage(CloudDriver.getInstance().getPrefix() + "§cThe player §e" + playername + " §cis not added to maintenance§8!");
                            return;
                        }

                        NetworkConfig networkConfig = CloudDriver.getInstance().getNetworkConfig();

                        if (add) {
                            networkConfig.getWhitelistedPlayers().add(playername);
                        } else {
                            networkConfig.getWhitelistedPlayers().remove(playername);
                        }

                        networkConfig.update();

                        ICloudPlayer cachedPlayer = CloudDriver.getInstance().getPlayerManager().getCachedObject(playername);

                        if (!add && cachedPlayer != null) {
                            if (!CloudDriver
                                    .getInstance()
                                    .getNetworkConfig()
                                    .getWhitelistedPlayers()
                                    .contains(player.getName())
                                    && !player.hasPermission("cloudsystem.network.maintenance")) {
                                cachedPlayer.kick(
                                        CloudDriver.getInstance()
                                                .getNetworkConfig()
                                                .getMessageConfig()
                                                .getMaintenanceNetwork()
                                                .replace("%prefix%",
                                                        CloudDriver.getInstance().getPrefix()));
                            }
                        }
                        player.sendMessage(CloudDriver.getInstance().getPrefix() + "§7The player §b" + playername + " §7was " + (add ? "§aadded to §7" : "§cremoved from §7") + "maintenance§8!");
                    } else if (args[0].equalsIgnoreCase("toggle")) {
                        if (args[1].equalsIgnoreCase("maintenance")) {
                            boolean maintenance = !CloudDriver.getInstance().getNetworkConfig().isMaintenance();
                            NetworkConfig networkConfig = CloudDriver.getInstance().getNetworkConfig();
                            networkConfig.setMaintenance(maintenance);
                            networkConfig.update();
                            player.sendMessage(CloudDriver.getInstance().getPrefix() + (maintenance ? "§7The Network is now in §amaintenance§8!" : "§7The Network is §cno longer §7in maintenance§8!"));
                        } else if (args[1].equalsIgnoreCase("notify")) {
                            PlayerInformation playerData = CloudDriver.getInstance().getPermissionPool().getPlayerInformation(player.getUniqueId());
                            if (playerData == null) {
                                player.sendMessage(CloudDriver.getInstance().getPrefix() + "§cYour §eCloudPlayerData §ccouldn't be found!");
                                return;
                            }
                            boolean change = !playerData.isNotifyServerStart();
                            playerData.setNotifyServerStart(change);
                            CloudDriver.getInstance().getPermissionPool().updatePlayer(playerData);
                            CloudDriver.getInstance().getPermissionPool().update();
                            player.sendMessage(CloudDriver.getInstance().getPrefix() + (change ? "§7You will §anow receive §7Server notifications§8!" : "§7You will §cno longer receive §7Server notifications§8!"));
                        } else {
                            help(player);
                        }
                    } else if (args[0].equalsIgnoreCase("log")) {
                        String s = args[1];
                        IService service = CloudDriver.getInstance().getServiceManager().getCachedObject(s);

                        if (service == null) {
                            player.sendMessage(CloudDriver.getInstance().getPrefix() + "§cThe service §e" + s + " §cseems not to be online!");
                            return;
                        }

                        player.sendMessage(CloudDriver.getInstance().getPrefix() + "§7Please wait a moment while §bHytoraCloud §7is uploading the log of §b" + service.getName() + "§8...");

                        String logUrl = service.getLogUrl();
                        player.sendMessage(CloudDriver.getInstance().getPrefix() + "§7The §blog for §7service §b" + service.getName() + " §7was uploaded to §a" + logUrl + " §8!");

                    } else if (args[0].equalsIgnoreCase("tps")) {
                        String groupname = args[1];
                        IServiceGroup group = CloudDriver.getInstance().getServiceManager().getServiceGroup(groupname);
                        if (group == null) {
                            player.sendMessage(CloudDriver.getInstance().getPrefix() + "§cThe group §e" + groupname + " §cseems not to exist!");
                            return;
                        }
                        ICloudPlayer cachedPlayer = CloudDriver.getInstance().getPlayerManager().getCachedObject(player.getName());

                        cachedPlayer.sendMessage(CloudDriver.getInstance().getPrefix() + "§7TPS of group §b" + group.getName() + "§8:");

                        for (IService service : group.getServices()) {
                            player.sendMessage("  §8» §b" + service.getName() + " §8┃ §7" + service.getTPS());
                        }

                    } else if (args[0].equalsIgnoreCase("stop")) {
                        String s = args[1];
                        IService IService = CloudDriver.getInstance().getServiceManager().getCachedObject(s);
                        if (IService == null) {
                            player.sendMessage(CloudDriver.getInstance().getPrefix() + "§cThe service §e" + s + " §cseems not to be online!");
                            return;
                        }
                        player.sendMessage(CloudDriver.getInstance().getPrefix() + "§7Trying to stop service §c" + IService.getName() + "§8...");
                        CloudDriver.getInstance().getServiceManager().stopService(IService);
                    } else if (args[0].equalsIgnoreCase("stopGroup")) {
                        String g = args[1];
                        IServiceGroup group = CloudDriver.getInstance().getServiceManager().getServiceGroup(g);
                        if (group == null) {
                            player.sendMessage(CloudDriver.getInstance().getPrefix() + "§cThe group §e" + g + " §cseems not to exist!");
                            return;
                        }
                        CloudDriver.getInstance().getServiceManager().shutdownAll(group);
                        player.sendMessage(CloudDriver.getInstance().getPrefix() + "§7Trying to stop services from group §c" + group.getName() + "§8...");
                    } else if (args[0].equalsIgnoreCase("list")) {
                        if (args[1].equalsIgnoreCase("maintenance")) {
                            if (CloudDriver.getInstance().getNetworkConfig().getWhitelistedPlayers().isEmpty()) {
                                player.sendMessage(CloudDriver.getInstance().getPrefix() + "§cThere are no whitelisted players yet!");
                                return;
                            }
                            player.sendMessage(CloudDriver.getInstance().getPrefix() + "§7Players:");
                            for (String whitelistedPlayer : CloudDriver.getInstance().getNetworkConfig().getWhitelistedPlayers()) {
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
                            IServiceGroup group = CloudDriver.getInstance().getServiceManager().getServiceGroup(groupname);

                            if (group == null) {
                                player.sendMessage(CloudDriver.getInstance().getPrefix() + "§cThe group §e" + groupname + " §cseems not to exist!");
                                return;
                            }

                            player.sendMessage(CloudDriver.getInstance().getPrefix() + "§7Trying to start §b" + id + " §7new services of group §a" + group.getName() + "§8...");

                            for (int i = 0; i < id; i++) CloudDriver.getInstance().getServiceManager().startService(group);
                        } catch (NumberFormatException e) {
                            player.sendMessage(CloudDriver.getInstance().getPrefix() + "§cPlease provide a valid number!");
                        }
                    } else if (args[0].equalsIgnoreCase("toggle") && args[1].equalsIgnoreCase("maintenance")) {
                        String groupname = args[2];
                        IServiceGroup serviceGroup = CloudDriver.getInstance().getServiceManager().getServiceGroup(groupname);
                        if (serviceGroup == null) {
                            player.sendMessage(CloudDriver.getInstance().getPrefix() + "§cThe group §e" + groupname + " §cdoesn't exist!");
                            return;
                        }
                        boolean maintenance = !serviceGroup.isMaintenance();
                        serviceGroup.setMaintenance(maintenance);
                        serviceGroup.update();
                        player.sendMessage(CloudDriver.getInstance().getPrefix() + "§7The group §b" + serviceGroup.getName() + " §7is " + (maintenance ? "§anow in maintenance§8!" : "§cno longer in maintenance§8!"));
                    } else if (args[0].equalsIgnoreCase("copy")) {
                        String servername = args[1];
                        String templatename = args[2];
                        IService IService = CloudDriver.getInstance().getServiceManager().getCachedObject(servername);
                        if (IService == null) {
                            player.sendMessage(CloudDriver.getInstance().getPrefix() + "§cThe service §e" + servername + " §cdoesn't exist!");
                            return;
                        }

                        CloudDriver.getInstance().copyTemplate(IService, templatename);
                        player.sendMessage(CloudDriver.getInstance().getPrefix() + "§7Copied §b" + servername + " §7into template §b" + templatename);
                    } else {
                        this.help(player);
                    }
                } else if (args.length == 4) {
                    if (args[0].equalsIgnoreCase("copy")) {
                        String servername = args[1];
                        String templatename = args[2];
                        String directory = args[3];
                        IService IService = CloudDriver.getInstance().getServiceManager().getCachedObject(servername);
                        if (IService == null) {
                            player.sendMessage(CloudDriver.getInstance().getPrefix() + "§cThe service §e" + servername + " §cdoesn't exist!");
                            return;
                        }

                        CloudDriver.getInstance().copyTemplate(IService, templatename, directory);
                        player.sendMessage(CloudDriver.getInstance().getPrefix() + "§7Copied folder §e" + directory + " §7from Service §b" + servername + " §7into template §b" + templatename);
                    } else {
                        this.help(player);
                    }
                } else {
                    this.help(player);
                }
            } else {
                player.sendMessage(CloudDriver.getInstance().getPrefix() + "§cYou aren't allowed to perform this command!");
            }
        }
    }

    public void help(ICloudPlayer player) {
        player.sendMessage("§bHytoraCloud §7Help§8:");
        player.sendMessage("§8§m--------------------------------------");
        player.sendMessage("  §8» §b/cloud list <maintenance> §8┃ §7Lists whitelisted players");
        player.sendMessage("  §8» §b/cloud ver §8┃ §7Shows the current version");
        player.sendMessage("  §8» §b/cloud ping §8┃ §7Shows the ping from this server to the cloud");
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
            return Utils.toStringList(CloudDriver.getInstance().getServiceManager().getCachedObjects());
        } else if (args[0].equalsIgnoreCase("stopGroup") || args[0].equalsIgnoreCase("run") || args[0].equalsIgnoreCase("tps")) {
            return Utils.toStringList(CloudDriver.getInstance().getServiceManager().getCachedGroups());
        } else if (args[0].equalsIgnoreCase("maintenance")) {
            if (args.length == 2) {
                List<String> groups = Utils.toStringList(CloudDriver.getInstance().getServiceManager().getCachedGroups());
                groups.add("switch");
                return groups;
            } else if (args.length == 3) {
                return ImmutableList.of("true", "false");
            }
        }
        return ImmutableList.of("list", "tps", "ver", "shutdown", "rl", "maintenance", "run", "stop", "stopGroup", "copy", "log", "toggle", "stats");
    }
}
