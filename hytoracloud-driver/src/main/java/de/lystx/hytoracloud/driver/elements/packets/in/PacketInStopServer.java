package de.lystx.hytoracloud.driver.elements.packets.in;

import de.lystx.hytoracloud.driver.elements.service.Service;
import io.thunder.packet.Packet;
import io.thunder.packet.PacketBuffer;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;


@AllArgsConstructor @Getter
public class PacketInStopServer extends Packet implements Serializable {

    private Service service;

    @Override
    public void read(PacketBuffer buf) {
        service = Service.readFromBuf(buf);
    }

    @Override
    public void write(PacketBuffer buf) {
        service.writeToBuf(buf);
    }

}
