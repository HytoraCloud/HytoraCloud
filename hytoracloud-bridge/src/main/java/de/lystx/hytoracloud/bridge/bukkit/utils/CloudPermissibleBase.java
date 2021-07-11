package de.lystx.hytoracloud.bridge.bukkit.utils;

import de.lystx.hytoracloud.bridge.bukkit.HytoraCloudBukkitBridge;
import de.lystx.hytoracloud.driver.CloudDriver;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissibleBase;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.util.*;

@Getter
public class CloudPermissibleBase extends PermissibleBase {

    /**
     * The player for this base
     */
    private final Player player;

    /**
     * The failed tries
     */
    private int tries;

    /**
     * The cached perms
     */
    private Map<String, PermissionAttachmentInfo> perms;

    public CloudPermissibleBase(Player player) {
        super(player);
        this.perms = new HashMap<>();
        this.player = player;
        this.tries = 0;

        player.setOp(false);
        this.recalculatePermissions();
    }

    /**
     *  GETTING VALUES
     * @returns boolean (true | false)
     */

    @Override
    public boolean isOp() {
        return this.hasPermission("*");
    }

    @Override
    public boolean isPermissionSet(String name) {
        return this.hasPermission(name);
    }

    @Override
    public boolean isPermissionSet(Permission perm) {
        return this.hasPermission(perm.getName());
    }

    @Override
    public Set<PermissionAttachmentInfo> getEffectivePermissions() {
        return new HashSet<>(perms.values());
    }

    @Override
    public boolean hasPermission(Permission perm) {
        return this.hasPermission(perm.getName());
    }

    @Override
    public boolean hasPermission(String inName) {
        return CloudDriver.getInstance().getPermissionPool().hasPermission(player.getUniqueId(), inName);
    }


    /**
     * SETTING VLAUES
     */

    @Override
    public void recalculatePermissions() {
        this.perms = new HashMap<>();
        if (player == null || Bukkit.getPlayer(this.player.getName()) == null) {
            return;
        }
        try {


            CloudDriver
                    .getInstance()
                    .getPermissionPool()
                        .updatePermissions(
                                player.getUniqueId(),
                                player.getAddress().getAddress().getHostAddress(),
                                s -> perms
                                        .put(
                                                s,
                                                new PermissionAttachmentInfo(
                                                        this,
                                                        s,
                                                        new PermissionAttachment(
                                                                HytoraCloudBukkitBridge
                                                                        .getInstance(),
                                                                this
                                                        ), true)));
        } catch (NullPointerException e) {
            tries += 1;
            CloudDriver.getInstance().getScheduler().scheduleDelayedTask(this::recalculatePermissions, 5L);
            if (tries >= 5) {
                System.out.println("[CloudBridge] Something went wrong while recalculating permissions of a player!");
                tries = 0;
            }
        }
    }


}
