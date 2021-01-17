package de.lystx.cloudapi.bukkit;

import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudapi.bukkit.command.ServiceCommand;
import de.lystx.cloudapi.bukkit.handler.*;
import de.lystx.cloudapi.bukkit.listener.NPCListener;
import de.lystx.cloudapi.bukkit.listener.PlayerListener;
import de.lystx.cloudapi.bukkit.manager.nametag.NametagManager;
import de.lystx.cloudapi.bukkit.manager.npc.NPCManager;
import de.lystx.cloudapi.bukkit.manager.sign.SignManager;
import de.lystx.cloudapi.proxy.manager.CloudManager;
import de.lystx.cloudsystem.library.elements.packets.in.service.PacketPlayInRegister;
import de.lystx.cloudsystem.library.service.permission.impl.PermissionGroup;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayerData;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class CloudServer extends JavaPlugin {

    @Getter
    private static CloudServer instance;
    private CloudAPI cloudAPI;

    private CloudManager manager;
    private SignManager signManager;
    private NametagManager nametagManager;
    private NPCManager npcManager;

    @Override
    public void onEnable() {
        instance = this;
        this.cloudAPI = new CloudAPI();
        this.manager = new CloudManager(this.cloudAPI);
        this.signManager = new SignManager(this);
        this.nametagManager = new NametagManager();
        this.npcManager = new NPCManager();

        this.cloudAPI.setNametags(true);
        this.cloudAPI.setUseChat(true);

        this.cloudAPI.getCloudClient().registerPacketHandler(new PacketHandlerBukkitCommand(this.cloudAPI));
        this.cloudAPI.getCloudClient().registerPacketHandler(new PacketHandlerBukkitStop(this.cloudAPI));
        this.cloudAPI.getCloudClient().registerPacketHandler(new PacketHandlerBukkitSignSystem(this.cloudAPI));
        this.cloudAPI.getCloudClient().registerPacketHandler(new PacketHandlerBukkitServerUpdate(this.cloudAPI));
        this.cloudAPI.getCloudClient().registerPacketHandler(new PacketHandlerBukkitCloudPlayerHandler(this.cloudAPI));
        this.cloudAPI.getCloudClient().registerPacketHandler(new PacketHandlerBukkitSubChannel(this.cloudAPI));
        this.cloudAPI.getCloudClient().registerPacketHandler(new PacketHandlerBukkitNPCs(this.cloudAPI));

        this.getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        this.getServer().getPluginManager().registerEvents(new NPCListener(), this);
        this.getCommand("service").setExecutor(new ServiceCommand());

        this.cloudAPI.sendPacket(new PacketPlayInRegister(this.cloudAPI.getService()));
        Runtime.getRuntime().addShutdownHook(new Thread(() -> this.cloudAPI.shutdown()));
    }

    @Override
    public void onDisable() {
        int animationScheduler = this.signManager.getSignUpdater().getAnimationScheduler();
        Bukkit.getScheduler().cancelTask(animationScheduler);
    }

    public void updatePermissions(Player player) {
        CloudPlayerData data = this.cloudAPI.getPermissionPool().getPlayerDataOrDefault(player.getName());
        if (data.isDefault() || !this.cloudAPI.getPermissionPool().isRankValid(player.getName())) {
            data.setUuid(player.getUniqueId());
            try {
                data.setIpAddress(player.getAddress().getHostName());
            } catch (NullPointerException e) {
                data.setIpAddress("127.0.0.1");
            }
            if (!this.cloudAPI.getPermissionPool().isAvailable()) {
                return;
            }
            this.cloudAPI.getPermissionPool().updatePlayerData(player.getName(), data);
            this.cloudAPI.getPermissionPool().update(this.cloudAPI.getCloudClient());
        }
        PermissionGroup group = this.cloudAPI.getPermissionPool().getPermissionGroupFromName(data.getPermissionGroup());
        if (group == null) {
            this.cloudAPI.messageCloud(this.cloudAPI.getService().getName(), "§cTried updating permissions for §e" + player.getName() + " §cbut his permissionGroup wasn't found!");
            return;
        }
        for (PermissionAttachmentInfo effectivePermission : player.getEffectivePermissions()) {
            player.addAttachment(this, effectivePermission.getPermission(), false);
        }
        player.setOp(false);
        boolean op = false;
        for (String permission : group.getPermissions()) {
            if (permission.equalsIgnoreCase("*")) {
                op = true;
            }
            player.addAttachment(this, permission, true);
        }

        for (String inheritance : group.getInheritances()) {
            PermissionGroup permissionGroup = this.cloudAPI.getPermissionPool().getPermissionGroupFromName(inheritance);
            for (String permission : permissionGroup.getPermissions()) {
                if (permission.equalsIgnoreCase("*")) {
                    op = true;
                }
                player.addAttachment(this, permission, true);
            }
        }
        for (String permission : data.getPermissions()) {
            if (permission.equalsIgnoreCase("*")) {
                op = true;
            }
            player.addAttachment(this, permission, true);
        }
        if (op) player.setOp(true);
    }
}
