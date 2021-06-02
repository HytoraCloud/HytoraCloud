package de.lystx.hytoracloud.driver.service.config.impl.proxy;

import io.thunder.packet.PacketBuffer;
import io.thunder.utils.objects.ThunderObject;
import io.vson.elements.object.Objectable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter @AllArgsConstructor
public class Motd implements Serializable, ThunderObject {

    private boolean enabled;
    private String firstLine;
    private String secondLine;
    private String protocolString;
    private String versionString;

    @Override
    public void write(PacketBuffer buf) {
        buf.writeBoolean(enabled);
        buf.writeString(firstLine);
        buf.writeString(secondLine);
        buf.writeString(protocolString);
        buf.writeString(versionString);
    }

    @Override
    public void read(PacketBuffer buf) {
        enabled = buf.readBoolean();
        firstLine = buf.readString();
        secondLine = buf.readString();
        protocolString = buf.readString();
        versionString = buf.readString();
    }
}
