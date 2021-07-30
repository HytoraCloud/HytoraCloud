package de.lystx.hytoracloud.launcher.cloud.handler.other;

import de.lystx.hytoracloud.driver.cloudservices.managing.player.impl.OfflinePlayer;
import de.lystx.hytoracloud.driver.commons.minecraft.other.NetworkInfo;
import de.lystx.hytoracloud.driver.commons.packets.out.PacketOutPing;
import de.lystx.hytoracloud.driver.commons.requests.base.DriverRequest;
import de.lystx.hytoracloud.launcher.cloud.CloudSystem;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.commons.packets.in.request.perms.PacketRequestPermissionGroupAdd;
import de.lystx.hytoracloud.driver.commons.packets.in.request.perms.PacketRequestPermissionGroupGet;
import de.lystx.hytoracloud.driver.cloudservices.managing.permission.impl.PermissionGroup;
import de.lystx.hytoracloud.driver.cloudservices.managing.permission.impl.PermissionPool;
import de.lystx.hytoracloud.networking.elements.packet.Packet;
import de.lystx.hytoracloud.networking.elements.packet.handler.PacketHandler;
import lombok.AllArgsConstructor;
import lombok.Getter;
import de.lystx.hytoracloud.networking.elements.packet.response.ResponseStatus;

import java.util.UUID;
import java.util.function.Consumer;


@Getter
public class CloudHandlerRequest implements PacketHandler {

    private final CloudSystem cloudSystem;

    public CloudHandlerRequest(CloudSystem cloudSystem) {
        this.cloudSystem = cloudSystem;

        CloudDriver.getInstance().getRequestManager().registerRequestHandler(new Consumer<DriverRequest<?>>() {
            @Override
            public void accept(DriverRequest<?> driverRequest) {
                if (driverRequest.equalsIgnoreCase("CLOUD_GET_TPS")) {
                    driverRequest.createResponse().data(new NetworkInfo().formatTps(CloudDriver.getInstance().getTicksPerSecond().getTPS())).send();
                }
            }
        });
    }

    public void handle(Packet packet) {
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

        }
    }
}
