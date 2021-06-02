package de.lystx.hytoracloud.driver.elements.packets.in;

import de.lystx.hytoracloud.driver.elements.packets.both.PacketCommunication;
import de.lystx.hytoracloud.driver.elements.service.ServiceGroup;
import io.thunder.packet.PacketBuffer;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter @AllArgsConstructor
public class PacketInUpdateServiceGroup extends PacketCommunication implements Serializable {

    private ServiceGroup serviceGroup;


    @Override
    public void read(PacketBuffer buf) {
        super.read(buf);

        serviceGroup = ServiceGroup.readFromBuf(buf);
    }

    @Override
    public void write(PacketBuffer buf) {
        super.write(buf);

        serviceGroup.writeToBuf(buf);
    }
}
