package de.lystx.hytoracloud.launcher.cloud.handler.player;

import de.lystx.hytoracloud.driver.service.player.impl.PlayerInformation;
import de.lystx.hytoracloud.launcher.cloud.CloudSystem;
import de.lystx.hytoracloud.driver.elements.events.player.CloudPlayerChangeServerCloudEvent;
import de.lystx.hytoracloud.driver.elements.packets.both.other.PacketCallEvent;
import de.lystx.hytoracloud.driver.elements.packets.both.player.PacketUpdatePlayer;
import de.lystx.hytoracloud.driver.elements.packets.in.PacketInPlayerExecuteCommand;
import de.lystx.hytoracloud.driver.elements.packets.request.other.PacketRequestPing;
import de.lystx.hytoracloud.driver.service.player.ICloudPlayerManager;
import io.thunder.packet.Packet;
import io.thunder.packet.handler.PacketHandler;

import de.lystx.hytoracloud.driver.service.config.stats.StatsService;

import de.lystx.hytoracloud.driver.service.permission.PermissionService;
import de.lystx.hytoracloud.driver.service.permission.impl.PermissionPool;
import de.lystx.hytoracloud.driver.service.player.impl.CloudPlayer;


import de.lystx.hytoracloud.driver.CloudDriver;
import io.thunder.packet.impl.response.ResponseStatus;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;

@AllArgsConstructor
public class PacketHandlerCloudPlayer implements PacketHandler {

    private final CloudSystem cloudSystem;


    @SneakyThrows
    @Override
    public void handle(Packet packet) {
        ICloudPlayerManager iCloudPlayerManager = CloudDriver.getInstance().getCloudPlayerManager();
        if (packet instanceof PacketCallEvent) {
            PacketCallEvent packetCallEvent = (PacketCallEvent)packet;
            if (packetCallEvent.getCloudEvent() instanceof CloudPlayerChangeServerCloudEvent) {
                CloudPlayerChangeServerCloudEvent serverEvent = (CloudPlayerChangeServerCloudEvent)packetCallEvent.getCloudEvent();
                try {
                    CloudPlayer cloudPlayer = CloudDriver.getInstance().getCloudPlayerManager().getCachedPlayer(serverEvent.getCloudPlayer().getName());
                    if (cloudPlayer != null) {
                        cloudPlayer.setService(CloudDriver.getInstance().getServiceManager().getService(serverEvent.getNewServer()));
                        cloudPlayer.update();
                    }
                } catch (NullPointerException e) {
                    //IGNORING
                }
            }
        }
        if (packet instanceof PacketUpdatePlayer) {
            PacketUpdatePlayer packetUpdatePlayer = (PacketUpdatePlayer)packet;
            CloudPlayer cloudPlayer = packetUpdatePlayer.getCloudPlayer();
            PlayerInformation playerInformation = cloudPlayer.getPlayerInformation();

            System.out.println("{INCOMING} " + cloudPlayer.getName() + "@" + cloudPlayer.getUniqueId() + " [" + playerInformation.getAllPermissionGroups().size() + "]");
            try {
                PermissionPool permissionPool = CloudDriver.getInstance().getPermissionPool();
                permissionPool.updatePlayer(playerInformation);
                CloudDriver.getInstance().setPermissionPool(permissionPool);
                CloudDriver.getInstance().getInstance(PermissionService.class).save();

                CloudDriver.getInstance().getCloudPlayerManager().update(cloudPlayer);
                CloudDriver.getInstance().reload();

            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }

        if (packet instanceof PacketInPlayerExecuteCommand) {

            this.cloudSystem.getInstance(StatsService.class).getStatistics().add("executedCommands");

        }
        if (packet instanceof PacketRequestPing) {

            PacketRequestPing packetOutPingRequest = (PacketRequestPing)packet;
            CloudPlayer cloudPlayer = iCloudPlayerManager.getCachedPlayer(packetOutPingRequest.getUuid());


            int ping = CloudDriver.getInstance().getConnection().transferToResponse(new PacketRequestPing(cloudPlayer.getUniqueId())).get(0).asInt();

            packet.respond(ResponseStatus.SUCCESS, ping);

        }
    }
}
