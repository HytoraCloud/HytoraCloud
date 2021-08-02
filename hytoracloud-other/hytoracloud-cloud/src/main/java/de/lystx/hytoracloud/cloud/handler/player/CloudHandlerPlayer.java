package de.lystx.hytoracloud.cloud.handler.player;

import de.lystx.hytoracloud.driver.commons.packets.both.player.PacketUnregisterPlayer;
import de.lystx.hytoracloud.driver.commons.packets.both.player.PacketUpdatePlayer;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.ICloudPlayerManager;
import de.lystx.hytoracloud.driver.commons.requests.base.DriverRequest;
import de.lystx.hytoracloud.driver.commons.storage.JsonObject;
import de.lystx.hytoracloud.networking.elements.packet.Packet;
import de.lystx.hytoracloud.networking.elements.packet.handler.PacketHandler;

import de.lystx.hytoracloud.driver.cloudservices.managing.player.ICloudPlayer;


import de.lystx.hytoracloud.driver.CloudDriver;
import lombok.SneakyThrows;

import java.util.UUID;
import java.util.function.Consumer;

public class CloudHandlerPlayer implements PacketHandler {


    public CloudHandlerPlayer() {
        CloudDriver.getInstance().getRequestManager().registerRequestHandler(new Consumer<DriverRequest<?>>() {
            @Override
            public void accept(DriverRequest<?> driverRequest) {
                JsonObject<?> document = driverRequest.getDocument();
                if (driverRequest.equalsIgnoreCase("PLAYER_GET_SYNC_UUID")) {
                    try {
                        UUID uniqueId = UUID.fromString(document.getString("uniqueId"));

                        ICloudPlayer cachedObject = CloudDriver.getInstance().getPlayerManager().getCachedObject(uniqueId);
                        driverRequest.createResponse().data(cachedObject).send();
                    } catch (Exception e) {
                        driverRequest.createResponse().exception(e).send();
                    }
                } else if (driverRequest.equalsIgnoreCase("PLAYER_GET_SYNC_NAME")) {
                    try {
                        String name = document.getString("name");

                        ICloudPlayer cachedObject = CloudDriver.getInstance().getPlayerManager().getCachedObject(name);
                        driverRequest.createResponse().data(cachedObject).send();
                    } catch (Exception e) {
                        driverRequest.createResponse().exception(e).send();
                    }
                }
            }
        });
    }

    @SneakyThrows
    @Override
    public void handle(Packet packet) {
        ICloudPlayerManager playerManager = CloudDriver.getInstance().getPlayerManager();

        if (packet instanceof PacketUpdatePlayer) {

            PacketUpdatePlayer p = (PacketUpdatePlayer)packet;
            ICloudPlayer cloudPlayer = p.getCloudPlayer();

            if (cloudPlayer == null) {
                return;
            }

            //Intern method decides if updating or registering
            playerManager.update(cloudPlayer);
            CloudDriver.getInstance().sendPacket(packet);
        }

        if (packet instanceof PacketUnregisterPlayer) {

            PacketUnregisterPlayer packetUnregisterPlayer = (PacketUnregisterPlayer)packet;

            ICloudPlayer cachedPlayer = playerManager.getCachedObject(packetUnregisterPlayer.getName());

            if (cachedPlayer != null) {
                playerManager.unregisterPlayer(cachedPlayer);
            }

            CloudDriver.getInstance().sendPacket(packet);
        }

    }
}
