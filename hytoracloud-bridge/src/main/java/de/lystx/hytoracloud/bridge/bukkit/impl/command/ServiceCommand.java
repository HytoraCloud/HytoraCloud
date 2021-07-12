package de.lystx.hytoracloud.bridge.bukkit.impl.command;

import com.sun.management.OperatingSystemMXBean;
import de.lystx.hytoracloud.driver.commons.interfaces.RunTaskSynchronous;
import de.lystx.hytoracloud.driver.utils.utillity.PropertyObject;
import de.lystx.hytoracloud.driver.commons.packets.both.other.PacketInformation;
import de.lystx.hytoracloud.driver.commons.service.IServiceGroup;
import de.lystx.hytoracloud.driver.commons.enums.cloud.ServiceState;
import de.lystx.hytoracloud.driver.cloudservices.managing.command.base.CloudCommandSender;
import de.lystx.hytoracloud.driver.cloudservices.managing.command.base.Command;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.impl.ICloudPlayer;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.utils.reflection.Reflections;
import org.bukkit.*;

import java.lang.management.ManagementFactory;
import java.text.DecimalFormat;
import java.util.*;

@RunTaskSynchronous(false)
public class ServiceCommand {

    private boolean executed = false;

    @Command(name = "service", description = "Bukkit server command", aliases = {"hs", "cloudserver"})
    public void execute(CloudCommandSender sender, String[] args) {
        if (sender instanceof ICloudPlayer) {
            ICloudPlayer player = (ICloudPlayer) sender;

            if (player.hasPermission("cloudsystem.command.service")) {
                if (args.length == 1) {
                    if (args[0].equalsIgnoreCase("info")) {
                        if (!this.executed) {
                            this.executed = true;
                            player.sendMessage(CloudDriver.getInstance().getPrefix() + "§7Loading §bService Infos§8...");
                            CloudDriver.getInstance().getModules(); //Requesting Modules to load
                        }
                        long used = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed() / 1048576L;
                        long max = Runtime.getRuntime().maxMemory() / 1048576L;
                        String format = new DecimalFormat("##.##").format(((OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean()).getProcessCpuLoad() * 100);
                        player.sendMessage("§bCloudService Info§8:");
                        player.sendMessage("§8§m--------------------------------------");
                        player.sendMessage("  §8» §bServer §8┃ §7" + CloudDriver.getInstance().getCurrentService().getName());
                        player.sendMessage("  §8» §bState §8┃ §7" + CloudDriver.getInstance().getCurrentService().getState().getColor() + CloudDriver.getInstance().getCurrentService().getState());
                        player.sendMessage("  §8» §bID §8┃ §7" + CloudDriver.getInstance().getCurrentService().getId());
                        player.sendMessage("  §8» §bUUID §8┃ §7" + CloudDriver.getInstance().getCurrentService().getUniqueId());
                        player.sendMessage("  §8» §bPort §8┃ §7" + CloudDriver.getInstance().getCurrentService().getPort());
                        player.sendMessage("  §8» §bReceiver §8┃ §7" + CloudDriver.getInstance().getConnection().remoteAddress().toString());
                        player.sendMessage("  §8» §bConnected to §8┃ §7" + CloudDriver.getInstance().getHost());
                        player.sendMessage("  §8» §bTemplate §8┃ §7" + CloudDriver.getInstance().getCurrentService().getGroup().getTemplate().getName());
                        player.sendMessage("  §8» §bMemory §8┃ §7" + used + "§7/§7" + max + "MB");
                        player.sendMessage("  §8» §bInternal CPU Usage §8┃ §7" + format);
                        PropertyObject properties = CloudDriver.getInstance().getCurrentService().getProperties();
                        if (!properties.keySet().isEmpty()) {
                            for (String key : properties.keySet()) {
                                player.sendMessage("  §8» §b" + key + " §8┃ §7" + properties.get(key));
                            }
                        }
                        player.sendMessage("§8§m--------------------------------------");
                    } else if (args[0].equalsIgnoreCase("removeSign")) {
                        if (!CloudDriver.getInstance().getCurrentService().getGroup().isLobby()) {
                            player.sendMessage(CloudDriver.getInstance().getPrefix() + "§cThis is not a Lobby server!");
                            return;
                        }
                        if (CloudDriver.getInstance().getModule("module-serverSelector") == null) {
                            player.sendMessage(CloudDriver.getInstance().getPrefix() + "§cThe §eServerSelector-Module §cis not in modules folder!");
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

                            CloudDriver.getInstance().sendPacket(packetInformation);
                        } else {
                            player.sendMessage(CloudDriver.getInstance().getPrefix() + "§cThe block you are looking at, is not a sign!");
                        }
                    } else if (args[0].equalsIgnoreCase("removeNPC")) {
                        if (CloudDriver.getInstance().getModule("module-serverSelector") == null) {
                            player.sendMessage(CloudDriver.getInstance().getPrefix() + "§cThe §eServerSelector-Module §cis not in modules folder!");
                            return;
                        }
                        if (!CloudDriver.getInstance().getCurrentService().getGroup().isLobby()) {
                            player.sendMessage(CloudDriver.getInstance().getPrefix() + "§cThis is not a Lobby server!");
                            return;
                        }
                        if (CloudDriver.getInstance().getBukkit().isNewVersion()) {
                            player.sendMessage(CloudDriver.getInstance().getPrefix() + "§cNPCs are not supported on version §e" + CloudDriver.getInstance().getBukkit().getVersion() + "§c!");
                            return;
                        }

                        List<UUID> uuidList = CloudDriver.getInstance().getImplementedData().getList("uuidList", UUID.class);
                        if (!uuidList.contains(player.getUniqueId())) {
                            uuidList.add(player.getUniqueId());
                            player.sendMessage(CloudDriver.getInstance().getPrefix() + "§7Leftclick the §bNPC §7you want to remove§8! §cTo cancel type this command §eagain§c!");
                        } else {
                            uuidList.remove(player.getUniqueId());

                            player.sendMessage(CloudDriver.getInstance().getPrefix() + "§cDeletion was §ecancelled§c!");
                        }
                        CloudDriver.getInstance().getImplementedData().put("uuidList", uuidList);
                    } else {
                        this.help(player);
                    }
                } else if (args.length == 2) {
                    if (args[0].equalsIgnoreCase("createSign")) {
                        if (!CloudDriver.getInstance().getCurrentService().getGroup().isLobby()) {
                            player.sendMessage(CloudDriver.getInstance().getPrefix() + "§cThis is not a Lobby server!");
                            return;
                        }
                        if (CloudDriver.getInstance().getModule("module-serverSelector") == null) {
                            player.sendMessage(CloudDriver.getInstance().getPrefix() + "§cThe §eServerSelector-Module §cis not in modules folder!");
                            return;
                        }
                        String serverGroup = args[1];
                        IServiceGroup group = CloudDriver.getInstance().getServiceManager().getServiceGroup(serverGroup);
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

                                CloudDriver.getInstance().sendPacket(packetInformation);

                            } else {
                                player.sendMessage(CloudDriver.getInstance().getPrefix() + "§cThe block you are looking at, is not a sign!");
                            }
                        } else {
                            player.sendMessage(CloudDriver.getInstance().getPrefix() + "§cThe group §e" + serverGroup + " §cdoesn't exist!");
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
                            player.sendMessage(CloudDriver.getInstance().getPrefix() + "§cPlease provide a valid §eServiceState§c!");
                            return;
                        }
                        ServiceState state = ServiceState.valueOf(name.toUpperCase());

                        CloudDriver.getInstance().getBukkit().setServiceState(state);
                        CloudDriver.getInstance().getBukkit().update();
                        player.sendMessage(CloudDriver.getInstance().getPrefix() + "§7You set the ServiceState of this service to " + state.getColor() + state.name());
                    } else {
                        this.help(player);
                    }
                } else if (args.length == 4) {
                    if (args[0].equalsIgnoreCase("createNPC")) {
                        if (!CloudDriver.getInstance().getCurrentService().getGroup().isLobby()) {
                            player.sendMessage(CloudDriver.getInstance().getPrefix() + "§cThis is not a Lobby server!");
                            return;
                        }
                        if (CloudDriver.getInstance().getBukkit().isNewVersion()) {
                            player.sendMessage(CloudDriver.getInstance().getPrefix() + "§cNPCs are not supported on version §e" + Reflections.getVersion() + "§c!");
                            return;
                        }
                        if (CloudDriver.getInstance().getModule("module-serverSelector") == null) {
                            player.sendMessage(CloudDriver.getInstance().getPrefix() + "§cThe §eServerSelector-Module §cis not in modules folder!");
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

                        CloudDriver.getInstance().sendPacket(packetInformation);
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

    public void help(ICloudPlayer ICloudPlayer) {
        ICloudPlayer.sendMessage("§bCloudService §7Help§8:");
        ICloudPlayer.sendMessage("§8§m--------------------------------------");
        ICloudPlayer.sendMessage("  §8» §b/service info §8┃ §7Displays info about this service");
        if (CloudDriver.getInstance().getModule("module-serverSelector") != null) {
            ICloudPlayer.sendMessage("  §8» §b/service createSign <Group> §8┃ §7Creates a CloudSign");
            ICloudPlayer.sendMessage("  §8» §b/service removeSign §8┃ §7Removes a CloudSign");
            ICloudPlayer.sendMessage("  §8» §b/service createNPC <Group> <Name> <Skin> §8┃ §7Creates an NPC");
            ICloudPlayer.sendMessage("  §8» §b/service removeNPC §8┃ §7Removes an NPC");
        }
        ICloudPlayer.sendMessage("  §8» §b/service setState <State> §8┃ §7Sets the state of this service");
        ICloudPlayer.sendMessage("§8§m--------------------------------------");


    }
}
