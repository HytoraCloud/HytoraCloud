package de.lystx.hytoracloud.driver.service.config.impl.fallback;

import io.thunder.packet.PacketBuffer;
import io.thunder.utils.objects.ThunderObject;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter @AllArgsConstructor
public class Fallback implements ThunderObject {

    /**
     * The priority
     */
    private int priority;

    /**
     * The group it belongs to
     */
    private String groupName;

    /**
     * The permission to access it
     */
    private String permission;

    @Override
    public void write(PacketBuffer buf) {
        buf.writeInt(priority);
        buf.writeString(groupName);
        buf.nullSafe().writeString(permission);
    }

    @Override
    public void read(PacketBuffer buf) {
        priority = buf.readInt();
        groupName = buf.readString();
        permission = buf.nullSafe().readString();
    }
}
