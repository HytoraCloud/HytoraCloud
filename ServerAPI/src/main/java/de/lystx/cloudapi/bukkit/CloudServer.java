package de.lystx.cloudapi.bukkit;

import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudapi.bukkit.command.ServiceCommand;
import de.lystx.cloudapi.bukkit.handler.*;
import de.lystx.cloudapi.bukkit.listener.CloudListener;
import de.lystx.cloudapi.bukkit.listener.NPCListener;
import de.lystx.cloudapi.bukkit.listener.PlayerListener;
import de.lystx.cloudapi.bukkit.manager.labymod.LabyMod;
import de.lystx.cloudapi.bukkit.manager.nametag.NametagManager;
import de.lystx.cloudapi.bukkit.manager.npc.NPCManager;
import de.lystx.cloudapi.bukkit.manager.sign.SignManager;
import de.lystx.cloudapi.proxy.manager.CloudManager;
import de.lystx.cloudsystem.library.elements.packets.in.service.PacketPlayInRegister;
import de.lystx.cloudsystem.library.service.permission.impl.PermissionEntry;
import de.lystx.cloudsystem.library.service.permission.impl.PermissionGroup;
import de.lystx.cloudsystem.library.service.permission.impl.PermissionPool;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayerData;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

@Getter
public class CloudServer extends JavaPlugin {

    @Getter
    private static CloudServer instance;
    private CloudAPI cloudAPI;

    private CloudManager manager;
    private SignManager signManager;
    private NametagManager nametagManager;
    private NPCManager npcManager;
    private LabyMod labyMod;

    @Override
    public void onEnable() {
        instance = this;
        this.cloudAPI = new CloudAPI();
        this.manager = new CloudManager(this.cloudAPI);
        this.signManager = new SignManager(this);
        this.nametagManager = new NametagManager();
        this.npcManager = new NPCManager();
        this.labyMod = new LabyMod(this.cloudAPI);

        this.cloudAPI.getCloudClient().registerPacketHandler(new PacketHandlerBukkitStop(this.cloudAPI));
        this.cloudAPI.getCloudClient().registerPacketHandler(new PacketHandlerBukkitSignSystem(this.cloudAPI));
        this.cloudAPI.getCloudClient().registerPacketHandler(new PacketHandlerBukkitServerUpdate(this.cloudAPI));
        this.cloudAPI.getCloudClient().registerPacketHandler(new PacketHandlerBukkitCloudPlayerHandler(this.cloudAPI));
        this.cloudAPI.getCloudClient().registerPacketHandler(new PacketHandlerBukkitSubChannel(this.cloudAPI));
        this.cloudAPI.getCloudClient().registerPacketHandler(new PacketHandlerBukkitNPCs(this.cloudAPI));
        this.cloudAPI.getCloudClient().registerPacketHandler(new PacketHandlerTPS(this.cloudAPI));

        this.getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        this.getServer().getPluginManager().registerEvents(new NPCListener(), this);
        this.getCommand("service").setExecutor(new ServiceCommand());
        this.cloudAPI.getCloudClient().registerHandler(new CloudListener());

        this.cloudAPI.sendPacket(new PacketPlayInRegister(this.cloudAPI.getService()));
    }

    @Override
    public void onDisable() {
        if (this.cloudAPI.getCloudClient().isConnected()) {
            this.cloudAPI.disconnect();
        }
        int animationScheduler = this.signManager.getSignUpdater().getAnimationScheduler();
        Bukkit.getScheduler().cancelTask(animationScheduler);
    }

    public void executeCommand(String command) {
        Bukkit.getScheduler().runTask(this, () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command));
        //cloudAPI.getScheduler().runTask(() -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command));
    }


    public void updatePermissions(Player player) {

        player.setOp(false);
        if (this.cloudAPI.getCloudPlayers().get(player.getName()) != null) {
            this.cloudAPI.updatePermissions(player.getName(), player.getUniqueId(), player.getAddress().getAddress().getHostAddress(), s -> {
                if (s.equalsIgnoreCase("*")) {
                    player.setOp(true);
                }
                player.addAttachment(this, s, true);
            });
        } else {
            this.cloudAPI.getScheduler().scheduleDelayedTask(() -> this.updatePermissions(player), 5L);
        }
    }
}
