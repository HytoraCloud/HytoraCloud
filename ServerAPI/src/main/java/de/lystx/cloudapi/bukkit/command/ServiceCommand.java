package de.lystx.cloudapi.bukkit.command;

import com.sun.management.OperatingSystemMXBean;
import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudapi.bukkit.CloudServer;
import de.lystx.cloudapi.bukkit.utils.Reflections;
import de.lystx.cloudsystem.library.elements.packets.both.other.PacketInformation;
import de.lystx.cloudsystem.library.elements.service.ServiceGroup;
import de.lystx.cloudsystem.library.enums.ServiceState;
import de.lystx.cloudsystem.library.service.command.base.CloudCommandSender;
import de.lystx.cloudsystem.library.service.command.base.Command;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayer;
import de.lystx.cloudsystem.library.Cloud;
import org.bukkit.*;

import java.lang.management.ManagementFactory;
import java.text.DecimalFormat;
import java.util.*;

public class ServiceCommand {

    private boolean executed = false;

    @Command(name = "service", description = "Bukkit server command", aliases = {"hs", "cloudserver"})
    public void execute(CloudCommandSender sender, String[] args) {
        if (sender instanceof CloudPlayer) {
            CloudPlayer player = (CloudPlayer) sender;

            if (player.hasPermission("cloudsystem.command.service")) {
                if (args.length == 1) {
                    if (args[0].equalsIgnoreCase("info")) {
                        if (!this.executed) {
                            this.executed = true;
                            player.sendMessage(CloudAPI.getInstance().getPrefix() + "§7Loading §bService Infos§8...");
                        }
                        DecimalFormat DECIMAL_FORMAT = new DecimalFormat("##.##");
                        long used = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed() / 1048576L;
                        long max = Runtime.getRuntime().maxMemory() / 1048576L;
                        String format = DECIMAL_FORMAT.format(((OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean()).getProcessCpuLoad() * 100);
                        player.sendMessage("§bCloudService Info§8:");
                        player.sendMessage("§8§m--------------------------------------");
                        player.sendMessage("  §8» §bServer §8┃ §7" + CloudAPI.getInstance().getService().getName());
                        player.sendMessage("  §8» §bState §8┃ §7" + CloudAPI.getInstance().getService().getServiceState().getColor() + CloudAPI.getInstance().getService().getServiceState());
                        player.sendMessage("  §8» §bID §8┃ §7" + CloudAPI.getInstance().getService().getServiceID());
                        player.sendMessage("  §8» §bUUID §8┃ §7" + CloudAPI.getInstance().getService().getUniqueId());
                        player.sendMessage("  §8» §bPort §8┃ §7" + CloudAPI.getInstance().getService().getPort());
                        player.sendMessage("  §8» §bReceiver §8┃ §7" + CloudAPI.getInstance().getService().getServiceGroup().getReceiver());
                        player.sendMessage("  §8» §bConnected to §8┃ §7" + CloudAPI.getInstance().getService().getHost() + "§8:§7" + CloudAPI.getInstance().getService().getCloudPort());
                        player.sendMessage("  §8» §bTemplate §8┃ §7" + CloudAPI.getInstance().getService().getServiceGroup().getTemplate());
                        player.sendMessage("  §8» §bMemory §8┃ §7" + used + "§7/§7" + max + "MB");
                        player.sendMessage("  §8» §bInternal CPU Usage §8┃ §7" + format);
                        if (!CloudAPI.getInstance().getService().getProperties().isEmpty()) {
                            CloudAPI.getInstance().getService().getProperties().forEach((key, value) -> player.sendMessage("  §8» §b" + key + " §8┃ §7" + value));
                        }
                        player.sendMessage("§8§m--------------------------------------");
                    } else if (args[0].equalsIgnoreCase("removeSign")) {
                        if (!CloudAPI.getInstance().getService().getServiceGroup().isLobby()) {
                            player.sendMessage(CloudAPI.getInstance().getPrefix() + "§cThis is not a Lobby server!");
                            return;
                        }
                        if (CloudAPI.getInstance().getModule("module-serverSelector") == null) {
                            player.sendMessage(CloudAPI.getInstance().getPrefix() + "§cThe §eServerSelector-Module §cis not in modules folder!");
                            return;
                        }
                        Set<Material> materials = new HashSet<>();
                        materials.add(Material.AIR);
                        Location loc = Bukkit.getPlayer(player.getName()).getTargetBlock(materials, 5).getLocation();
                        if (loc.getBlock().getType().equals(Material.WALL_SIGN)) {

                            Map<String, Object> map = new HashMap<>();
                            map.put("player", player.getName());
                            map.put("location", loc.serialize());
                            PacketInformation packetInformation = new PacketInformation("deleteSign", map);

                            CloudAPI.getInstance().sendPacket(packetInformation);
                        } else {
                            player.sendMessage(CloudAPI.getInstance().getPrefix() + "§cThe block you are looking at, is not a sign!");
                        }
                    } else if (args[0].equalsIgnoreCase("removeNPC")) {
                        if (!CloudAPI.getInstance().getService().getServiceGroup().isLobby()) {
                            player.sendMessage(CloudAPI.getInstance().getPrefix() + "§cThis is not a Lobby server!");
                            return;
                        }
                        if (CloudAPI.getInstance().getModule("module-serverSelector") == null) {
                            player.sendMessage(CloudAPI.getInstance().getPrefix() + "§cThe §eServerSelector-Module §cis not in modules folder!");
                            return;
                        }
                        if (CloudServer.getInstance().isNewVersion()) {
                            player.sendMessage(CloudAPI.getInstance().getPrefix() + "§cNPCs are not supported on version §e" + Reflections.getVersion() + "§c!");
                            return;
                        }

                        if (!Cloud.getInstance().getNpcDeleterList().contains(player.getUniqueId())) {
                            Cloud.getInstance().getNpcDeleterList().add(player.getUniqueId());
                            player.sendMessage(CloudAPI.getInstance().getPrefix() + "§7Leftclick the §bNPC §7you want to remove§8! §cTo cancel type this command §eagain§c!");
                        } else {
                            Cloud.getInstance().getNpcDeleterList().remove(player.getUniqueId());
                            player.sendMessage(CloudAPI.getInstance().getPrefix() + "§cDeletion was §ecancelled§c!");
                        }
                    } else {
                        this.help(player);
                    }
                } else if (args.length == 2) {
                    if (args[0].equalsIgnoreCase("createSign")) {
                        if (!CloudAPI.getInstance().getService().getServiceGroup().isLobby()) {
                            player.sendMessage(CloudAPI.getInstance().getPrefix() + "§cThis is not a Lobby server!");
                            return;
                        }
                        if (CloudAPI.getInstance().getModule("module-serverSelector") == null) {
                            player.sendMessage(CloudAPI.getInstance().getPrefix() + "§cThe §eServerSelector-Module §cis not in modules folder!");
                            return;
                        }
                        String serverGroup = args[1];
                        ServiceGroup group = CloudAPI.getInstance().getNetwork().getServiceGroup(serverGroup);
                        if (group != null) {
                            Set<Material> materials = new HashSet<>();
                            materials.add(Material.AIR);
                            Location loc = Bukkit.getPlayer(player.getName()).getTargetBlock(materials, 5).getLocation();
                            if (loc.getBlock().getType().equals(Material.WALL_SIGN)) {

                                Map<String, Object> map = new HashMap<>();
                                map.put("player", player.getName());
                                map.put("location", loc.serialize());
                                map.put("group", group.getName());
                                PacketInformation packetInformation = new PacketInformation("createSign", map);

                                CloudAPI.getInstance().sendPacket(packetInformation);

                            } else {
                                player.sendMessage(CloudAPI.getInstance().getPrefix() + "§cThe block you are looking at, is not a sign!");
                            }
                        } else {
                            player.sendMessage(CloudAPI.getInstance().getPrefix() + "§cThe group §e" + serverGroup + " §cdoesn't exist!");
                        }
                    } else if (args[0].equalsIgnoreCase("setState")) {
                        String name = args[1];
                        boolean cont = false;
                        for (ServiceState value : ServiceState.values()) {
                            if (value.name().equalsIgnoreCase(name)) {
                                cont = true;
                                break;
                            }
                        }
                        if (!cont) {
                            player.sendMessage(CloudAPI.getInstance().getPrefix() + "§cPlease provide a valid §eServiceState§c!");
                            return;
                        }
                        ServiceState state = ServiceState.valueOf(name.toUpperCase());
                        CloudServer.getInstance().getManager().setServiceState(state);
                        CloudServer.getInstance().getManager().update();
                        player.sendMessage(CloudAPI.getInstance().getPrefix() + "§7You set the ServiceState of this service to " + state.getColor() + state.name());
                    } else {
                        this.help(player);
                    }
                } else if (args.length == 4) {
                    if (args[0].equalsIgnoreCase("createNPC")) {
                        if (!CloudAPI.getInstance().getService().getServiceGroup().isLobby()) {
                            player.sendMessage(CloudAPI.getInstance().getPrefix() + "§cThis is not a Lobby server!");
                            return;
                        }
                        if (CloudServer.getInstance().isNewVersion()) {
                            player.sendMessage(CloudAPI.getInstance().getPrefix() + "§cNPCs are not supported on version §e" + Reflections.getVersion() + "§c!");
                            return;
                        }
                        if (CloudAPI.getInstance().getModule("module-serverSelector") == null) {
                            player.sendMessage(CloudAPI.getInstance().getPrefix() + "§cThe §eServerSelector-Module §cis not in modules folder!");
                            return;
                        }
                        String groupName = args[1];

                        Map<String, Object> map = new HashMap<>();

                        map.put("key", "createNPC");
                        map.put("player", player.getName());
                        map.put("skin", args[3]);
                        map.put("name", args[2].replace("_", " "));
                        map.put("loc", Bukkit.getPlayer(player.getName()).getLocation().serialize());
                        map.put("group", groupName);
                        PacketInformation packetInformation = new PacketInformation("createNPC", map);

                        CloudAPI.getInstance().sendPacket(packetInformation);
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

    public void help(CloudPlayer cloudPlayer) {
        cloudPlayer.sendMessage("§bCloudService §7Help§8:");
        cloudPlayer.sendMessage("§8§m--------------------------------------");
        cloudPlayer.sendMessage("  §8» §b/service info §8┃ §7Displays info about this service");
        if (CloudAPI.getInstance().getModule("module-serverSelector") != null) {
            cloudPlayer.sendMessage("  §8» §b/service createSign <Group> §8┃ §7Creates a CloudSign");
            cloudPlayer.sendMessage("  §8» §b/service removeSign §8┃ §7Removes a CloudSign");
            cloudPlayer.sendMessage("  §8» §b/service createNPC <Group> <Name> <Skin> §8┃ §7Creates an NPC");
            cloudPlayer.sendMessage("  §8» §b/service removeNPC §8┃ §7Removes an NPC");
        }
        cloudPlayer.sendMessage("  §8» §b/service setState <State> §8┃ §7Sets the state of this service");
        cloudPlayer.sendMessage("§8§m--------------------------------------");


    }
}
