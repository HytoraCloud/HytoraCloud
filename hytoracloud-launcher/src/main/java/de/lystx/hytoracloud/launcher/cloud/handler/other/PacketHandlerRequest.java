package de.lystx.hytoracloud.launcher.cloud.handler.other;

import de.lystx.hytoracloud.driver.elements.other.JsonBuilder;
import de.lystx.hytoracloud.driver.service.player.impl.PlayerInformation;
import de.lystx.hytoracloud.launcher.cloud.CloudSystem;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.elements.packets.request.other.PacketRequestModules;
import de.lystx.hytoracloud.driver.elements.packets.request.perms.PacketRequestPermissionGroup;
import de.lystx.hytoracloud.driver.elements.packets.request.perms.PacketRequestPermissionGroupAdd;
import de.lystx.hytoracloud.driver.elements.packets.request.perms.PacketRequestPermissionGroupGet;
import de.lystx.hytoracloud.driver.elements.packets.request.property.PacketRequestAddProperty;
import de.lystx.hytoracloud.driver.elements.packets.request.property.PacketRequestGetProperty;
import de.lystx.hytoracloud.driver.service.module.Module;
import de.lystx.hytoracloud.driver.service.module.ModuleInfo;
import de.lystx.hytoracloud.driver.service.module.ModuleService;
import de.lystx.hytoracloud.driver.service.permission.impl.PermissionGroup;
import de.lystx.hytoracloud.driver.service.permission.impl.PermissionPool;
import io.thunder.packet.Packet;
import io.thunder.packet.handler.PacketHandler;
import io.thunder.packet.impl.response.PacketRespond;
import io.thunder.packet.impl.response.ResponseStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;


@AllArgsConstructor @Getter
public class PacketHandlerRequest implements PacketHandler {

    private final CloudSystem cloudSystem;

    public void handle(Packet packet) {
        if (packet instanceof PacketRequestPermissionGroupAdd) {

            PacketRequestPermissionGroupAdd packetRequestPermissionGroupAdd = (PacketRequestPermissionGroupAdd) packet;

            UUID playerUUID = packetRequestPermissionGroupAdd.getPlayerUUID();
            PermissionPool permissionPool = CloudDriver.getInstance().getPermissionPool();

            PermissionGroup permissionGroup = permissionPool.getPermissionGroupByName(packetRequestPermissionGroupAdd.getGroup());

            if (permissionGroup == null) {
                packet.respond(ResponseStatus.FAILED, "The PermissionGroup " + packetRequestPermissionGroupAdd.getGroup() + " does not exist!");
                return;
            }

            permissionPool.addPermissionGroupToUser(playerUUID, permissionGroup, packetRequestPermissionGroupAdd.getI(), packetRequestPermissionGroupAdd.getValidality());
            permissionPool.update();

            packet.respond(ResponseStatus.SUCCESS, permissionGroup);
        } else if (packet instanceof PacketRequestPermissionGroupGet) {

            PacketRequestPermissionGroupGet packetRequestPermissionGroupGet = (PacketRequestPermissionGroupGet)packet;

            UUID playerUUID = packetRequestPermissionGroupGet.getPlayerUUID();

            PlayerInformation playerInformation = CloudDriver.getInstance().getPermissionPool().getPlayerInformation(playerUUID);

            if (playerInformation == null) {
                packet.respond(ResponseStatus.FAILED);
                return;
            }
            packet.respond(ResponseStatus.SUCCESS, playerInformation.getHighestPermissionGroup());

        } else if (packet instanceof PacketRequestAddProperty) {
            try {
                PacketRequestAddProperty packetRequestAddProperty = (PacketRequestAddProperty)packet;
                PlayerInformation offlinePlayer = CloudDriver.getInstance().getCloudPlayerManager().getOfflinePlayer(packetRequestAddProperty.getUniqueId());

                offlinePlayer.addProperty(packetRequestAddProperty.getName(), packetRequestAddProperty.getProperty());
                offlinePlayer.update();
                packet.respond(ResponseStatus.SUCCESS);
            } catch (Exception e) {
                packet.respond(ResponseStatus.FAILED);
            }

        } else if (packet instanceof PacketRequestGetProperty) {
            try {
                PacketRequestGetProperty packetRequestGetProperty = (PacketRequestGetProperty)packet;
                PlayerInformation offlinePlayer = CloudDriver.getInstance().getCloudPlayerManager().getOfflinePlayer(packetRequestGetProperty.getUniqueId());

                packet.respond(ResponseStatus.SUCCESS, offlinePlayer.getProperty(packetRequestGetProperty.getName()).toString());
            } catch (Exception e) {
                packet.respond(ResponseStatus.FAILED);
            }

        } else if (packet instanceof PacketRequestModules) {

            PacketRequestModules packetRequestModules = (PacketRequestModules)packet;

            List<ModuleInfo> list = new LinkedList<>();
            for (Module module : CloudDriver.getInstance().getInstance(ModuleService.class).getModules()) {
                list.add(module.getInfo());
            }
            packetRequestModules.setModuleInfos(list);
            CloudDriver.getInstance().sendPacket(packetRequestModules);
            
            //packet.respond(ResponseStatus.SUCCESS, list);


        } else if (packet instanceof PacketRequestPermissionGroup) {

            PacketRequestPermissionGroup packetRequestPermissionGroup = (PacketRequestPermissionGroup)packet;
            UUID name = packetRequestPermissionGroup.getName();

            PermissionGroup permissionGroup = CloudDriver.getInstance().getPermissionPool().getHighestPermissionGroup(name);

            if (permissionGroup != null) {
                packet.respond(ResponseStatus.SUCCESS, permissionGroup);
            } else {
                packet.respond(ResponseStatus.FAILED);
            }

        }
    }
}
