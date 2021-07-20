package de.lystx.hytoracloud.launcher.cloud.handler.other;

import de.lystx.hytoracloud.driver.cloudservices.managing.player.impl.OfflinePlayer;
import de.lystx.hytoracloud.driver.commons.packets.out.PacketOutPing;
import de.lystx.hytoracloud.launcher.cloud.CloudSystem;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.commons.packets.in.request.perms.PacketRequestPermissionGroup;
import de.lystx.hytoracloud.driver.commons.packets.in.request.perms.PacketRequestPermissionGroupAdd;
import de.lystx.hytoracloud.driver.commons.packets.in.request.perms.PacketRequestPermissionGroupGet;
import de.lystx.hytoracloud.driver.commons.packets.in.request.property.PacketRequestAddProperty;
import de.lystx.hytoracloud.driver.commons.packets.in.request.property.PacketRequestGetProperty;
import de.lystx.hytoracloud.driver.cloudservices.managing.permission.impl.PermissionGroup;
import de.lystx.hytoracloud.driver.cloudservices.managing.permission.impl.PermissionPool;
import net.hytora.networking.elements.packet.HytoraPacket;
import net.hytora.networking.elements.packet.handler.PacketHandler;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.hytora.networking.elements.packet.response.ResponseStatus;

import java.util.UUID;


@AllArgsConstructor @Getter
public class CloudHandlerRequest implements PacketHandler {

    private final CloudSystem cloudSystem;

    public void handle(HytoraPacket packet) {
        if (packet instanceof PacketRequestPermissionGroupAdd) {

            PacketRequestPermissionGroupAdd packetRequestPermissionGroupAdd = (PacketRequestPermissionGroupAdd) packet;

            UUID playerUUID = packetRequestPermissionGroupAdd.getPlayerUUID();
            PermissionPool permissionPool = CloudDriver.getInstance().getPermissionPool();

            PermissionGroup permissionGroup = permissionPool.getPermissionGroupByName(packetRequestPermissionGroupAdd.getGroup());

            if (permissionGroup == null) {
                packet.reply(ResponseStatus.FAILED, "The PermissionGroup " + packetRequestPermissionGroupAdd.getGroup() + " does not exist!");
                return;
            }

            permissionPool.addPermissionGroupToUser(playerUUID, permissionGroup, packetRequestPermissionGroupAdd.getI(), packetRequestPermissionGroupAdd.getValidality());
            permissionPool.update();

            packet.reply(component -> component.put("group", permissionGroup));

        } else if (packet instanceof PacketOutPing) {
            packet.reply(component -> component.put("ms", System.currentTimeMillis()));

        } else if (packet instanceof PacketRequestPermissionGroupGet) {

            PacketRequestPermissionGroupGet packetRequestPermissionGroupGet = (PacketRequestPermissionGroupGet)packet;

            UUID playerUUID = packetRequestPermissionGroupGet.getUuid();

            OfflinePlayer offlinePlayer = CloudDriver.getInstance().getPermissionPool().getCachedObject(playerUUID);

            if (offlinePlayer == null) {
                packet.reply(ResponseStatus.FAILED);
                return;
            }
            packet.reply(component -> component.put("group", offlinePlayer.getHighestPermissionGroup()));

        } else if (packet instanceof PacketRequestAddProperty) {
            try {
                PacketRequestAddProperty packetRequestAddProperty = (PacketRequestAddProperty)packet;
                OfflinePlayer offlinePlayer = CloudDriver.getInstance().getPlayerManager().getOfflinePlayer(packetRequestAddProperty.getPlayerUUID());

                offlinePlayer.addProperty(packetRequestAddProperty.getName(), packetRequestAddProperty.getProperty());
                offlinePlayer.update();
                packet.reply(ResponseStatus.SUCCESS);
            } catch (Exception e) {
                packet.reply(ResponseStatus.FAILED);
            }

        } else if (packet instanceof PacketRequestGetProperty) {
            try {
                PacketRequestGetProperty packetRequestGetProperty = (PacketRequestGetProperty)packet;
                OfflinePlayer offlinePlayer = CloudDriver.getInstance().getPlayerManager().getOfflinePlayer(packetRequestGetProperty.getPlayerUUID());

                packet.reply(ResponseStatus.SUCCESS, offlinePlayer.getProperty(packetRequestGetProperty.getName()).toString());
            } catch (Exception e) {
                packet.reply(ResponseStatus.FAILED);
            }

        } else if (packet instanceof PacketRequestPermissionGroup) {

            PacketRequestPermissionGroup packetRequestPermissionGroup = (PacketRequestPermissionGroup)packet;
            UUID name = packetRequestPermissionGroup.getUuid();

            PermissionGroup permissionGroup = CloudDriver.getInstance().getPermissionPool().getHighestPermissionGroup(name);

            if (permissionGroup != null) {
                packet.reply(component -> component.put("group", permissionGroup));
            } else {
                packet.reply(ResponseStatus.FAILED);
            }

        }
    }
}
