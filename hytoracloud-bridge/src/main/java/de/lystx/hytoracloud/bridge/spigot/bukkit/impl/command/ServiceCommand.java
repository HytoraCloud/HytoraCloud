package de.lystx.hytoracloud.bridge.spigot.bukkit.impl.command;

import com.sun.management.OperatingSystemMXBean;
import de.lystx.hytoracloud.bridge.spigot.bukkit.signselector.ServerSelector;
import de.lystx.hytoracloud.bridge.spigot.bukkit.signselector.manager.npc.impl.NPC;
import de.lystx.hytoracloud.driver.cloudservices.managing.command.base.ConsoleExecutor;
import de.lystx.hytoracloud.driver.cloudservices.managing.serverselector.npc.NPCMeta;
import de.lystx.hytoracloud.driver.cloudservices.managing.serverselector.sign.CloudSign;
import de.lystx.hytoracloud.driver.commons.interfaces.RunTaskSynchronous;
import de.lystx.hytoracloud.driver.commons.packets.in.PacketInCloudSignCreate;
import de.lystx.hytoracloud.driver.commons.packets.in.PacketInCloudSignDelete;
import de.lystx.hytoracloud.driver.commons.packets.in.PacketInNPCCreate;
import de.lystx.hytoracloud.driver.commons.enums.cloud.ServiceType;
import de.lystx.hytoracloud.driver.commons.service.IService;
import de.lystx.hytoracloud.driver.commons.storage.JsonObject;
import de.lystx.hytoracloud.driver.commons.storage.PropertyObject;
import de.lystx.hytoracloud.driver.commons.service.IServiceGroup;
import de.lystx.hytoracloud.driver.commons.enums.cloud.ServiceState;
import de.lystx.hytoracloud.driver.cloudservices.managing.command.base.CommandExecutor;
import de.lystx.hytoracloud.driver.cloudservices.managing.command.base.Command;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.impl.ICloudPlayer;
import de.lystx.hytoracloud.driver.CloudDriver;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

import java.lang.management.ManagementFactory;
import java.text.DecimalFormat;
import java.util.*;

@RunTaskSynchronous(false)
public class ServiceCommand {

    private boolean executed = false;

    @Command(
            name = "service",
            description = "Bukkit server command",
            aliases = {
                    "hs",
                    "cloudServer",
                    "hytoraServer"
            }
    )
    public void execute(CommandExecutor sender, String[] args) {
        if (sender instanceof ConsoleExecutor) {
            return;
        }
        if (sender instanceof ICloudPlayer) {
            ICloudPlayer player = (ICloudPlayer) sender;

            if (player.hasPermission("cloudsystem.command.service")) {
                if (args.length == 1) {
                    if (args[0].equalsIgnoreCase("info")) {
                        if (!this.executed) {
                            this.executed = true;
                            player.sendMessage(CloudDriver.getInstance().getPrefix() + "§7Loading §bService Infos§8...");
                        }

                        long used = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1048576L;

                        long max = Runtime.getRuntime().maxMemory() / 1048576L;
                        String format = new DecimalFormat("##.##").format(((OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean()).getProcessCpuLoad() * 100);
                        player.sendMessage("§bCloudService Info§8:");
                        player.sendMessage("§8§m--------------------------------------");
                        player.sendMessage("  §8» §bServer §8┃ §7" + CloudDriver.getInstance().getServiceManager().getCurrentService().getName());
                        player.sendMessage("  §8» §bState §8┃ §7" + CloudDriver.getInstance().getServiceManager().getCurrentService().getState().getColor() + CloudDriver.getInstance().getServiceManager().getCurrentService().getState());
                        player.sendMessage("  §8» §bID §8┃ §7" + CloudDriver.getInstance().getServiceManager().getCurrentService().getId());
                        player.sendMessage("  §8» §bUUID §8┃ §7" + CloudDriver.getInstance().getServiceManager().getCurrentService().getUniqueId());
                        player.sendMessage("  §8» §bPort §8┃ §7" + CloudDriver.getInstance().getServiceManager().getCurrentService().getPort());
                        player.sendMessage("  §8» §bReceiver §8┃ §7" + CloudDriver.getInstance().getConnection().remoteAddress().toString());
                        player.sendMessage("  §8» §bConnected to §8┃ §7" + CloudDriver.getInstance().getCloudAddress());
                        player.sendMessage("  §8» §bTemplate §8┃ §7" + CloudDriver.getInstance().getServiceManager().getCurrentService().getGroup().getCurrentTemplate().getName());
                        player.sendMessage("  §8» §bMemory §8┃ §7" + used + "§7/§7" + max + "MB");
                        player.sendMessage("  §8» §bInternal CPU Usage §8┃ §7" + format);
                        JsonObject<?> properties = CloudDriver.getInstance().getServiceManager().getCurrentService().getProperties();
                        if (!properties.keySet().isEmpty()) {
                            for (String key : properties.keySet()) {
                                player.sendMessage("  §8» §b" + key + " §8┃ §7" + properties.get(key));
                            }
                        }
                        player.sendMessage("§8§m--------------------------------------");
                    } else if (args[0].equalsIgnoreCase("removeSign")) {
                        if (!CloudDriver.getInstance().getServiceManager().getCurrentService().getGroup().isLobby()) {
                            player.sendMessage(CloudDriver.getInstance().getPrefix() + "§cThis is not a Lobby server!");
                            return;
                        }
                        Set<Material> materials = new HashSet<>();
                        materials.add(Material.AIR);
                        Location location = Bukkit.getPlayer(player.getName()).getTargetBlock(materials, 5).getLocation();

                        if (location.getBlock().getType().equals(Material.WALL_SIGN)) {
                            CloudSign cloudSign = ServerSelector.getInstance().getSignManager().getSignUpdater().getCloudSign(location);
                            if (cloudSign == null) {
                                player.sendMessage(CloudDriver.getInstance().getPrefix() + "§cThis §eCloudSign §cseems not to be registered!");
                                return;
                            }
                            Block block = Bukkit.getWorld(cloudSign.getWorld()).getBlockAt(cloudSign.getX(), cloudSign.getY(), cloudSign.getZ());
                            Sign signBlock = (Sign) block.getState();
                            signBlock.setLine(0, "§8§m------");
                            signBlock.setLine(1, "§4⚠⚠⚠⚠⚠");
                            signBlock.setLine(2, "§8» §cRemoved");
                            signBlock.setLine(3, "§8§m------");
                            signBlock.update(true);
                            ServerSelector.getInstance().getSignManager().getCloudSigns().remove(cloudSign);

                            CloudDriver.getInstance().sendPacket(new PacketInCloudSignDelete(cloudSign));

                            player.sendMessage(CloudDriver.getInstance().getPrefix() + "§7You removed a CloudSign for the group §b" + cloudSign.getGroup().toUpperCase());

                        } else {
                            player.sendMessage(CloudDriver.getInstance().getPrefix() + "§cThe block you are looking at, is not a sign!");
                        }
                    } else if (args[0].equalsIgnoreCase("removeNPC")) {
                        if (!CloudDriver.getInstance().getServiceManager().getCurrentService().getGroup().isLobby()) {
                            player.sendMessage(CloudDriver.getInstance().getPrefix() + "§cThis is not a Lobby server!");
                            return;
                        }
                        if (CloudDriver.getInstance().getBukkit().isNewVersion()) {
                            player.sendMessage(CloudDriver.getInstance().getPrefix() + "§cNPCs are not supported on version §e" + CloudDriver.getInstance().getBukkit().getVersion() + "§c!");
                            return;
                        }

                        List<UUID> uuidList = CloudDriver.getInstance().getImplementedData().getList("uuidList");
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
                        if (!CloudDriver.getInstance().getServiceManager().getCurrentService().getGroup().isLobby()) {
                            player.sendMessage(CloudDriver.getInstance().getPrefix() + "§cThis is not a Lobby server!");
                            return;
                        }
                        String serverGroup = args[1];
                        IServiceGroup group = CloudDriver.getInstance().getServiceManager().getServiceGroup(serverGroup);


                        if (group != null) {
                            if (group.getType() == ServiceType.PROXY) {
                                player.sendMessage(CloudDriver.getInstance().getPrefix() + "§cYou can not create §eCloudSigns §cfor §eProxyGroups§c!");
                                return;
                            }

                            Set<Material> materials = new HashSet<>();
                            materials.add(Material.AIR);
                            Location location = Bukkit.getPlayer(player.getName()).getTargetBlock(materials, 5).getLocation();
                            if (location.getBlock().getType().equals(Material.WALL_SIGN)) {

                                CloudSign sign = new CloudSign((int) location.getX(), (int) location.getY(), (int) location.getZ(), group.getName(), location.getWorld().getName());
                                if (ServerSelector.getInstance().getSignManager().getSignUpdater().getCloudSign(location) == null) {
                                    Block block = Bukkit.getWorld(sign.getWorld()).getBlockAt(sign.getX(), sign.getY(), sign.getZ());
                                    Sign signBlock = (Sign) block.getState();
                                    signBlock.setLine(0, "§8§m------");
                                    signBlock.setLine(1, "§b" + group.getName().toUpperCase());
                                    signBlock.setLine(2, "RELOADING...");
                                    signBlock.setLine(3, "§8§m------");
                                    signBlock.update(true);
                                    ServerSelector.getInstance().getSignManager().getCloudSigns().add(sign);

                                    CloudDriver.getInstance().sendPacket(new PacketInCloudSignCreate(sign));

                                    player.sendMessage(CloudDriver.getInstance().getPrefix() + "§7You created a CloudSign for the group §b" + group.getName());
                                } else {
                                    player.sendMessage(CloudDriver.getInstance().getPrefix() + "§cThe §eCloudSign §calready exists!");
                                }
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

                        IService service = CloudDriver.getInstance().getServiceManager().getCurrentService();
                        service.setState(state).addFutureListener(query -> {
                            if (query.isSuccess()) {
                                player.sendMessage(CloudDriver.getInstance().getPrefix() + "§7You set the ServiceState of this service to " + state.getColor() + state.name());
                            } else {
                                player.sendMessage(CloudDriver.getInstance().getPrefix() + "§cCouldn't set ServiceState to §e" + state.name() + "§c!");
                                player.sendMessage(CloudDriver.getInstance().getPrefix() + "§cException: §e" + query.getError().getClass().getName());
                            }
                        });
                    } else {
                        this.help(player);
                    }
                } else if (args.length == 4) {
                    if (args[0].equalsIgnoreCase("createNPC")) {
                        if (!CloudDriver.getInstance().getServiceManager().getCurrentService().getGroup().isLobby()) {
                            player.sendMessage(CloudDriver.getInstance().getPrefix() + "§cThis is not a Lobby server!");
                            return;
                        }
                        if (CloudDriver.getInstance().getBukkit().isNewVersion()) {
                            player.sendMessage(CloudDriver.getInstance().getPrefix() + "§cNPCs are not supported on version §e" + CloudDriver.getInstance().getBukkit().getVersion() + "§c!");
                            return;
                        }
                        String groupName = args[1];

                        String skin = args[3];
                        String name = args[2].replace("_", " ").replace("&", "§");
                        Location location = Bukkit.getPlayer(player.getName()).getLocation();

                        NPC npc = ServerSelector.getInstance().getNpcManager().getNPC(location);
                        if (npc != null) {
                            player.sendMessage(CloudDriver.getInstance().getPrefix() + "§cThere is already an §eNPC §cfor this location!");
                            return;
                        }
                        IServiceGroup group = CloudDriver.getInstance().getServiceManager().getServiceGroup(groupName);
                        if (group != null) {
                            CloudDriver.getInstance().sendPacket(new PacketInNPCCreate(new NPCMeta(UUID.randomUUID(), name, skin, groupName, location.serialize())));
                            ServerSelector.getInstance().getNpcManager().updateNPCS();
                            player.sendMessage(CloudDriver.getInstance().getPrefix() + "§7You created an NPC for the group §b" + group.getName() + " §7with skin §b" + skin + "§8!");
                        } else {
                            player.sendMessage(CloudDriver.getInstance().getPrefix() + "§cThe group §e" + groupName + " §cdoesn't exist!");
                        }
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

    public void help(ICloudPlayer cloudPlayer) {
        cloudPlayer.sendMessage("§bCloudService §7Help§8:");
        cloudPlayer.sendMessage("§8§m--------------------------------------");
        cloudPlayer.sendMessage("  §8» §b/service info §8┃ §7Displays info about this service");
        cloudPlayer.sendMessage("  §8» §b/service createSign <Group> §8┃ §7Creates a CloudSign");
        cloudPlayer.sendMessage("  §8» §b/service removeSign §8┃ §7Removes a CloudSign");
        cloudPlayer.sendMessage("  §8» §b/service createNPC <Group> <Name> <Skin> §8┃ §7Creates an NPC");
        cloudPlayer.sendMessage("  §8» §b/service removeNPC §8┃ §7Removes an NPC");
        cloudPlayer.sendMessage("  §8» §b/service setState <State> §8┃ §7Sets the state of this service");
        cloudPlayer.sendMessage("§8§m--------------------------------------");


    }
}
