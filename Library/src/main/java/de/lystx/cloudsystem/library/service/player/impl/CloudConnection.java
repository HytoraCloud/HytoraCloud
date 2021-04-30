package de.lystx.cloudsystem.library.service.player.impl;

import de.lystx.cloudsystem.library.elements.packets.both.player.PacketKickPlayer;
import de.lystx.cloudsystem.library.elements.service.Service;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import de.lystx.cloudsystem.library.service.util.CloudCache;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

/**
 * Class is used
 * to disconnect the player
 * and send packets
 */
@Getter @RequiredArgsConstructor
public class CloudConnection implements Serializable {

    private final UUID uniqueId;
    private final String name;
    private final String address;

    @Setter
    private Service start;

    /**
     * Closes connection
     * @param reason
     */
    public void disconnect(String reason) {
        this.sendPacket(new PacketKickPlayer(this.name, reason));
    }

    /**
     * Sends a packet
     * @param packet
     */
    public void sendPacket(Packet packet) {
        CloudCache.getInstance().getCurrentCloudExecutor().sendPacket(packet);
    }

}
