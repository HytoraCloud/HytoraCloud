package de.lystx.cloudapi.bukkit.command;

import com.sun.management.OperatingSystemMXBean;
import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudapi.bukkit.CloudServer;
import de.lystx.cloudapi.bukkit.manager.npc.impl.NPC;
import de.lystx.cloudsystem.library.elements.service.ServiceGroup;
import de.lystx.cloudsystem.library.enums.ServiceState;
import de.lystx.cloudsystem.library.service.serverselector.sign.base.CloudSign;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.lang.management.ManagementFactory;
import java.text.DecimalFormat;
import java.util.*;

public class ServiceCommand implements CommandExecutor {

    public static final List<UUID> deleters = new LinkedList<>();

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {

        if (commandSender instanceof Player) {
            Player player = (Player)commandSender;
            if (player.hasPermission("cloudsystem.command.service")) {
                if (args.length == 1) {
                    if (args[0].equalsIgnoreCase("info")) {
                        DecimalFormat DECIMAL_FORMAT = new DecimalFormat("##.##");
                        long used = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed() / 1048576L;
                        long max = Runtime.getRuntime().maxMemory() / 1048576L;
                        String format = DECIMAL_FORMAT.format(((OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean()).getProcessCpuLoad() * 100);
                        player.sendMessage("§bCloudServiceInfo §7Help§8:");
                        player.sendMessage("§8§m--------------------------------------");
                        player.sendMessage("  §8» §bServer §8┃ §7" + CloudAPI.getInstance().getService().getName());
                        player.sendMessage("  §8» §bState §8┃ §7" + CloudAPI.getInstance().getService().getServiceState().getColor() + CloudAPI.getInstance().getService().getServiceState());
                        player.sendMessage("  §8» §bID §8┃ §7" + CloudAPI.getInstance().getService().getServiceID());
                        player.sendMessage("  §8» §bTemplate §8┃ §7" + CloudAPI.getInstance().getService().getServiceGroup().getTemplate());
                        player.sendMessage("  §8» §bMemory §8┃ §7" + used + "§7/§7" + max + "MB");
                        player.sendMessage("  §8» §bInternal CPU Usage §8┃ §7" + format);
                        player.sendMessage("§8§m--------------------------------------");
                    } else if (args[0].equalsIgnoreCase("removeSign")) {
                        if (!CloudAPI.getInstance().getService().getServiceGroup().isLobby()) {
                            player.sendMessage(CloudAPI.getInstance().getPrefix() + "§cThis is not a Lobby server!");
                            return false;
                        }
                        Set<Material> materials = new HashSet<>();
                        materials.add(Material.AIR);
                        Location loc = player.getTargetBlock(materials, 5).getLocation();
                        if (loc.getBlock().getType().equals(Material.WALL_SIGN)) {
                            CloudSign cloudSign = CloudServer.getInstance().getSignManager().getSignUpdater().getCloudSign(loc);
                            if (cloudSign == null) {
                                player.sendMessage(CloudAPI.getInstance().getPrefix() + "§cThis §eCloudSign §cseems not to be registered!");
                                return false;
                            }
                            Block block = Bukkit.getWorld(cloudSign.getWorld()).getBlockAt(cloudSign.getX(), cloudSign.getY(), cloudSign.getZ());
                            Sign signBlock = (Sign) block.getState();
                            signBlock.setLine(0, "§8§m------");
                            signBlock.setLine(1, "§4⚠⚠⚠⚠⚠");
                            signBlock.setLine(2, "§8» §cRemoved");
                            signBlock.setLine(3, "§8§m------");
                            signBlock.update(true);
                            CloudServer.getInstance().getSignManager().getSignCreator().deleteSign(cloudSign);
                            player.sendMessage(CloudAPI.getInstance().getPrefix() + "§7You removed a CloudSign for the group §b" + cloudSign.getGroup().toUpperCase());
                        } else {
                            player.sendMessage(CloudAPI.getInstance().getPrefix() + "§cThe block you are looking at, is not a sign!");
                        }
                    } else if (args[0].equalsIgnoreCase("removeNPC")) {
                        if (!CloudAPI.getInstance().getService().getServiceGroup().isLobby()) {
                            player.sendMessage(CloudAPI.getInstance().getPrefix() + "§cThis is not a Lobby server!");
                            return false;
                        }

                        if (!deleters.contains(player.getUniqueId())) {
                            deleters.add(player.getUniqueId());
                            player.sendMessage(CloudAPI.getInstance().getPrefix() + "§7Leftclick the §bNPC §7you want to remove§8! §cTo cancel type this command §eagain§c!");
                        } else {
                            deleters.remove(player.getUniqueId());
                            player.sendMessage(CloudAPI.getInstance().getPrefix() + "§cDeletion was §ecancelled§c!");
                        }
                    } else {
                        this.help(player);
                    }
                } else if (args.length == 2) {
                    if (args[0].equalsIgnoreCase("createSign")) {
                        if (!CloudAPI.getInstance().getService().getServiceGroup().isLobby()) {
                            player.sendMessage(CloudAPI.getInstance().getPrefix() + "§cThis is not a Lobby server!");
                            return false;
                        }
                        String serverGroup = args[1];
                        ServiceGroup group = CloudAPI.getInstance().getNetwork().getServiceGroup(serverGroup);
                        if (group != null) {
                            Set<Material> materials = new HashSet<>();
                            materials.add(Material.AIR);
                            Location loc = player.getTargetBlock(materials, 5).getLocation();
                            if (loc.getBlock().getType().equals(Material.WALL_SIGN)) {
                                CloudSign sign = new CloudSign((int) loc.getX(), (int) loc.getY(), (int) loc.getZ(), group.getName(), loc.getWorld().getName());
                                if (CloudServer.getInstance().getSignManager().getSignUpdater().getCloudSign(loc) != null) {
                                    player.sendMessage(CloudAPI.getInstance().getPrefix() + "§cThe §eCloudSign §calready exists!");
                                    return false;
                                }
                                Block block = Bukkit.getWorld(sign.getWorld()).getBlockAt(sign.getX(), sign.getY(), sign.getZ());
                                Sign signBlock = (Sign) block.getState();
                                signBlock.setLine(0, "§8§m------");
                                signBlock.setLine(1, "§b" + serverGroup.toUpperCase());
                                signBlock.setLine(2, "RELOADING...");
                                signBlock.setLine(3, "§8§m------");
                                signBlock.update(true);
                                CloudServer.getInstance().getSignManager().getSignCreator().createSign(sign);
                                player.sendMessage(CloudAPI.getInstance().getPrefix() + "§7You created a CloudSign for the group §b" + group.getName());
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
                            }
                        }
                        if (!cont) {
                            player.sendMessage(CloudAPI.getInstance().getPrefix() + "§cPlease provide a valid §eServiceState§c!");
                            return false;
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
                            return false;
                        }
                        String groupName = args[1];
                        NPC npc = CloudServer.getInstance().getNpcManager().getNPC(player.getLocation());
                        if (npc != null) {
                            player.sendMessage(CloudAPI.getInstance().getPrefix() + "§cThere is already an §eNPC §cfor this location!");
                            return false;
                        }
                        ServiceGroup group = CloudAPI.getInstance().getNetwork().getServiceGroup(groupName);
                        if (group != null) {
                            CloudServer.getInstance().getNpcManager().createNPC(player.getLocation(), ChatColor.translateAlternateColorCodes('&',
                                    args[2].replace("_", " ")), group.getName(), args[3]);
                            player.sendMessage(CloudAPI.getInstance().getPrefix() + "§7You created an NPC for the group §b" + group.getName() + " §7with skin §b" + args[3] + "§8!");
                        } else {
                            player.sendMessage(CloudAPI.getInstance().getPrefix() + "§cThe group §e" + groupName + " §cdoesn't exist!");
                        }
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
        return false;
    }

    public void help(Player player) {

        player.sendMessage("§bCloudService §7Help§8:");
        player.sendMessage("§8§m--------------------------------------");
        player.sendMessage("  §8» §b/service info §8┃ §7Displays info about this service");
        player.sendMessage("  §8» §b/service createSign <Group> §8┃ §7Creates a CloudSign");
        player.sendMessage("  §8» §b/service removeSign §8┃ §7Removes a CloudSign");
        player.sendMessage("  §8» §b/service createNPC <Group> <Name> <Skin> §8┃ §7Creates an NPC");
        player.sendMessage("  §8» §b/service removeNPC §8┃ §7Removes an NPC");
        player.sendMessage("  §8» §b/service setState <State> §8┃ §7Sets the state of this service");
        player.sendMessage("§8§m--------------------------------------");

    }


}
