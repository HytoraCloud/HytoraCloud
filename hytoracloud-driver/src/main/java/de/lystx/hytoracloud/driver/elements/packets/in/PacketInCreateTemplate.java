package de.lystx.hytoracloud.driver.elements.packets.in;

import de.lystx.hytoracloud.driver.elements.service.ServiceGroup;
import io.thunder.packet.Packet;
import io.thunder.packet.PacketBuffer;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter @AllArgsConstructor
public class PacketInCreateTemplate extends Packet implements Serializable {

    private ServiceGroup serviceGroup;
    private String template;


    @Override
    public void read(PacketBuffer buf) {
        serviceGroup = ServiceGroup.readFromBuf(buf);
        template = buf.readString();
    }

    @Override
    public void write(PacketBuffer buf) {
        serviceGroup.writeToBuf(buf);
        buf.writeString(template);
    }
}
