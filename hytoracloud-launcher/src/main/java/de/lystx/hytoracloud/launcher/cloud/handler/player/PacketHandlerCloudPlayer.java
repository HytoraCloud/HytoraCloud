package de.lystx.hytoracloud.launcher.cloud.handler.player;

import de.lystx.hytoracloud.driver.elements.packets.both.player.PacketUpdatePlayer;
import de.lystx.hytoracloud.driver.elements.packets.request.other.PacketRequestPing;
import de.lystx.hytoracloud.driver.service.player.ICloudPlayerManager;
import net.hytora.networking.elements.packet.HytoraPacket;
import net.hytora.networking.elements.packet.handler.PacketHandler;

import de.lystx.hytoracloud.driver.service.player.impl.CloudPlayer;


import de.lystx.hytoracloud.driver.CloudDriver;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import net.hytora.networking.elements.packet.response.ResponseStatus;

@AllArgsConstructor
public class PacketHandlerCloudPlayer implements PacketHandler {


    @SneakyThrows
    @Override
    public void handle(HytoraPacket packet) {
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

            packet.reply(ResponseStatus.SUCCESS, CloudDriver.getInstance().getResponse(new PacketRequestPing(cloudPlayer.getUniqueId())).reply().getMessage());

        }
    }
}
