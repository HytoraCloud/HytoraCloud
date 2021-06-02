package de.lystx.hytoracloud.driver.elements.packets.in;

import de.lystx.hytoracloud.driver.elements.service.Service;
import io.thunder.packet.Packet;
import io.thunder.packet.PacketBuffer;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter @AllArgsConstructor
public class PacketInCopyTemplate extends Packet implements Serializable {

    private Service service;
    private String template;
    private String specificDirectory;

    @Override
    public void read(PacketBuffer buf) {
        service = Service.readFromBuf(buf);
        template = buf.readString();
        specificDirectory = buf.readString();
    }

    @Override
    public void write(PacketBuffer buf) {
        service.writeToBuf(buf);
        buf.writeString(template);
        buf.writeString(specificDirectory);
    }
}
