package de.lystx.hytoracloud.launcher.cloud.handler.player;

import de.lystx.hytoracloud.driver.commons.packets.both.player.PacketUnregisterPlayer;
import de.lystx.hytoracloud.driver.commons.packets.both.player.PacketUpdatePlayer;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.ICloudPlayerManager;
import de.lystx.hytoracloud.driver.commons.packets.in.request.other.PacketRequestPlayerNamed;
import de.lystx.hytoracloud.driver.commons.packets.in.request.other.PacketRequestPlayerUniqueId;
import de.lystx.hytoracloud.networking.elements.packet.Packet;
import de.lystx.hytoracloud.networking.elements.packet.handler.PacketHandler;

import de.lystx.hytoracloud.driver.cloudservices.managing.player.impl.ICloudPlayer;


import de.lystx.hytoracloud.driver.CloudDriver;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import de.lystx.hytoracloud.networking.elements.packet.response.ResponseStatus;

import java.util.UUID;

@AllArgsConstructor
public class CloudHandlerPlayer implements PacketHandler {


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

        if (packet instanceof PacketRequestPlayerNamed) {

            PacketRequestPlayerNamed packetRequestPlayerNamed = (PacketRequestPlayerNamed)packet;
            String name = packetRequestPlayerNamed.getName();

            ICloudPlayer playerObject = playerManager.getCachedObject(name);

            packet.reply(component -> component.put("player", playerObject));
        }

        if (packet instanceof PacketRequestPlayerUniqueId) {

            PacketRequestPlayerUniqueId packetRequestPlayerUniqueId = (PacketRequestPlayerUniqueId)packet;
            UUID uuid = packetRequestPlayerUniqueId.getUniqueId();

            ICloudPlayer playerObject = playerManager.getCachedObject(uuid);

            packet.reply(component -> component.put("player", playerObject));
        }


    }
}
