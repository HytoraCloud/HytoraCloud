package de.lystx.hytoracloud.driver.service.player.impl;

import de.lystx.hytoracloud.driver.elements.packets.both.player.PacketKickPlayer;
import de.lystx.hytoracloud.driver.CloudDriver;
import io.thunder.packet.PacketBuffer;
import io.thunder.utils.objects.ThunderObject;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import java.util.UUID;

/**
 * Class is used
 * to disconnect the player
 * and send packets
 */
@Getter @AllArgsConstructor
public class PlayerConnection implements Serializable, ThunderObject {

    /**
     * The UUId of this connection
     */
    private UUID uniqueId;

    /**
     * The name of this connection
     */
    private String name;

    /**
     * The address (host and port)
     */
    private String address;

    /**
     * The protocolVersion
     */
    private int protocolVersion;

    /**
     * If the connection is online (Cracked users)
     */
    private boolean online;

    /**
     * If its legacy or not
     */
    private boolean legacyMode;


    /**
     * Closes connection
     *
     * @param reason the reason for the disconnect
     */
    public void disconnect(String reason) {
        CloudDriver.getInstance().getConnection().sendPacket(new PacketKickPlayer(this.uniqueId, reason));
    }

    @Override
    public void write(PacketBuffer buf) {

        buf.writeString(name);
        buf.writeUUID(uniqueId);
        buf.writeString(address);
        buf.writeInt(protocolVersion);
        buf.writeBoolean(online);
        buf.writeBoolean(legacyMode);
    }

    @Override
    public void read(PacketBuffer buf) {
        name = buf.readString();
        uniqueId = buf.readUUID();
        address = buf.readString();
        protocolVersion = buf.readInt();
        online = buf.readBoolean();
        legacyMode = buf.readBoolean();
    }
}
