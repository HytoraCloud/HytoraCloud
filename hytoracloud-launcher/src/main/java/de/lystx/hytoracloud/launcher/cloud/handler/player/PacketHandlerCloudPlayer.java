package de.lystx.hytoracloud.launcher.cloud.handler.player;

import de.lystx.hytoracloud.launcher.cloud.CloudSystem;
import de.lystx.hytoracloud.driver.elements.packets.both.player.PacketUpdatePlayer;
import de.lystx.hytoracloud.driver.elements.packets.request.other.PacketRequestPing;
import de.lystx.hytoracloud.driver.service.player.ICloudPlayerManager;
import io.thunder.packet.Packet;
import io.thunder.packet.handler.PacketHandler;

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
        ICloudPlayerManager playerManager = CloudDriver.getInstance().getCloudPlayerManager();


        if (packet instanceof PacketUpdatePlayer) {

            PacketUpdatePlayer p = (PacketUpdatePlayer)packet;
            CloudPlayer cloudPlayer = p.getCloudPlayer();

            CloudPlayer cachedPlayer = playerManager.getCachedPlayer(cloudPlayer.getUniqueId());

            if (cachedPlayer == null) {
                //Player is not registered so we register...

                playerManager.registerPlayer(cloudPlayer);


            } else {
                //Player already online so updating...

                playerManager.update(cloudPlayer);
            }

            playerManager.sync();

        }

        if (packet instanceof PacketRequestPing) {

            PacketRequestPing packetOutPingRequest = (PacketRequestPing)packet;
            CloudPlayer cloudPlayer = playerManager.getCachedPlayer(packetOutPingRequest.getUuid());


            int ping = CloudDriver.getInstance().getConnection().transferToResponse(new PacketRequestPing(cloudPlayer.getUniqueId())).get(0).asInt();

            packet.respond(ResponseStatus.SUCCESS, ping);

        }
    }
}
