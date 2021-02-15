package de.lystx.cloudapi.bukkit.utils;

import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudapi.bukkit.CloudServer;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissibleBase;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.util.*;

@Getter
public class CloudPermissibleBase extends PermissibleBase {

    private final Player player;
    private int tries;
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
        return false;
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
        return CloudAPI.getInstance().getPermissionPool().hasPermission(player.getName(), inName);
    }


    /**
     * SETTING VLAUES
     */

    @Override
    public void recalculatePermissions() {
        this.perms = new HashMap<>();
        try {
            CloudAPI
                    .getInstance()
                    .updatePermissions(
                            player.getName(),
                            player.getUniqueId(),
                            player.getAddress().getAddress().getHostAddress(),
                            s -> perms
                                    .put(
                                            s,
                                            new PermissionAttachmentInfo(
                                                    CloudPermissibleBase.this,
                                                    s,
                                                    new PermissionAttachment(
                                                            CloudServer
                                                                    .getInstance(),
                                                            CloudPermissibleBase.this
                                                    ), true)));
        } catch (NullPointerException e) {
            tries += 1;
            CloudAPI.getInstance().getScheduler().scheduleDelayedTask(this::recalculatePermissions, 5L);
            if (tries >= 5) {
                System.out.println("[CloudAPI] Something went wrong while recalculating permissions of a player!");
                tries = 0;
            }
        }
    }


}
