package de.lystx.hytoracloud.launcher.cloud.handler.player;

import de.lystx.hytoracloud.driver.commons.events.player.other.DriverEventPlayerQuit;
import de.lystx.hytoracloud.driver.commons.packets.both.player.PacketUnregisterPlayer;
import de.lystx.hytoracloud.driver.commons.packets.both.player.PacketUpdatePlayer;
import de.lystx.hytoracloud.driver.commons.packets.in.request.other.PacketRequestPing;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.ICloudPlayerManager;
import net.hytora.networking.elements.packet.HytoraPacket;
import net.hytora.networking.elements.packet.handler.PacketHandler;

import de.lystx.hytoracloud.driver.cloudservices.managing.player.impl.ICloudPlayer;


import de.lystx.hytoracloud.driver.CloudDriver;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import net.hytora.networking.elements.packet.response.ResponseStatus;

@AllArgsConstructor
public class CloudHandlerPlayer implements PacketHandler {


    @SneakyThrows
    @Override
    public void handle(HytoraPacket packet) {
        ICloudPlayerManager playerManager = CloudDriver.getInstance().getCloudPlayerManager();

        if (packet instanceof PacketUpdatePlayer) {

            PacketUpdatePlayer p = (PacketUpdatePlayer)packet;

            //Intern method decides if updating or registering
            playerManager.update(p.getCloudPlayer());
        }

        if (packet instanceof PacketUnregisterPlayer) {

            PacketUnregisterPlayer packetUnregisterPlayer = (PacketUnregisterPlayer)packet;

            ICloudPlayer cachedPlayer = playerManager.getCachedPlayer(packetUnregisterPlayer.getName());

            if (cachedPlayer != null) {
                playerManager.unregisterPlayer(cachedPlayer);

            }

        }

        if (packet instanceof PacketRequestPing) {

            PacketRequestPing packetOutPingRequest = (PacketRequestPing)packet;
            ICloudPlayer cachedPlayer = playerManager.getCachedPlayer(packetOutPingRequest.getUuid());

            packet.reply(ResponseStatus.SUCCESS, CloudDriver.getInstance().getResponse(new PacketRequestPing(cachedPlayer.getUniqueId())).reply().getMessage());

        }
    }
}
