package de.lystx.hytoracloud.module.serverselector.cloud.manager.sign.base;


import io.thunder.packet.PacketBuffer;
import io.vson.elements.object.Objectable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import java.util.UUID;

/**
 * Serializable Class for the
 * CloudSign to work with it later
 */
@Getter @AllArgsConstructor
public class CloudSign implements Serializable, Objectable<CloudSign> {

    private final UUID uuid;
    private final Integer x;
    private final Integer y;
    private final Integer z;
    private final String group;
    private final String world;

    /**
     * Constructs a CloudSign
     * @param x
     * @param y
     * @param z
     * @param group
     * @param world
     */
    public CloudSign(Integer x, Integer y, Integer z, String group, String world) {
        this(UUID.randomUUID(), x, y, z, group, world);
    }

    public CloudSign() {
        this(0, 0, 0, "", "");
    }


    public void writeToBuf(PacketBuffer buf) {
        buf.writeUUID(uuid);
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
        buf.writeString(group);
        buf.writeString(world);
    }


    public static CloudSign readFromBuf(PacketBuffer buf) {
        UUID uniqueId = buf.readUUID();

        int x = buf.readInt();
        int y = buf.readInt();
        int z = buf.readInt();

        String group = buf.readString();
        String world = buf.readString();

        return new CloudSign(uniqueId, x, y, z, group, world);
    }
}
