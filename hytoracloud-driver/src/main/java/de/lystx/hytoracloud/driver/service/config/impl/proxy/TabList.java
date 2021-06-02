package de.lystx.hytoracloud.driver.service.config.impl.proxy;

import io.thunder.packet.PacketBuffer;
import io.thunder.utils.objects.ThunderObject;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter @AllArgsConstructor
public class TabList implements ThunderObject {

    private boolean enabled;
    private String header;
    private String footer;

    @Override
    public void write(PacketBuffer buf) {
        buf.writeBoolean(enabled);
        buf.writeString(header);
        buf.writeString(footer);
    }

    @Override
    public void read(PacketBuffer buf) {
        enabled = buf.readBoolean();
        header = buf.readString();
        footer = buf.readString();
    }
}
