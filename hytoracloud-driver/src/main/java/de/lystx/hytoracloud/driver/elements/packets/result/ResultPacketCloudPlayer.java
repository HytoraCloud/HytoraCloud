package de.lystx.hytoracloud.driver.elements.packets.result;

import de.lystx.hytoracloud.driver.CloudDriver;
import io.thunder.connection.data.ThunderConnection;
import io.thunder.packet.Packet;
import io.thunder.packet.PacketBuffer;


import de.lystx.hytoracloud.driver.service.player.impl.CloudPlayer;
import io.thunder.packet.impl.response.ResponseStatus;

import java.io.Serializable;
import java.util.UUID;

public class ResultPacketCloudPlayer extends Packet implements Serializable {

    private String name;
    private UUID uuid;

    public ResultPacketCloudPlayer(String name) {
        this.name = name;
        this.uuid = null;
    }
    public ResultPacketCloudPlayer(UUID uuid) {
        this.name = null;
        this.uuid = uuid;
    }

    @Override
    public void read(PacketBuffer buf) {
        name = buf.readString();
        if (!buf.readBoolean()) {
            uuid = buf.readUUID();
        }
    }

    @Override
    public void write(PacketBuffer buf) {
        buf.writeString(name);
        buf.writeBoolean(uuid == null);

        if (uuid != null) {
            buf.writeUUID(uuid);
        }
    }

    @Override
    public void handle(ThunderConnection thunderConnection) {
        CloudDriver cloudDriver = CloudDriver.getInstance();
        CloudPlayer cloudPlayer;

        if (this.uuid == null) {
            cloudPlayer = cloudDriver.getCloudPlayerManager().getCachedPlayer(this.name);
        } else {
            cloudPlayer = cloudDriver.getCloudPlayerManager().getCachedPlayer(this.uuid);
        }
        respond(ResponseStatus.SUCCESS, cloudPlayer);
    }

}
