package de.lystx.hytoracloud.driver.elements.packets.both.service;

import de.lystx.hytoracloud.driver.elements.packets.both.PacketCommunication;
import de.lystx.hytoracloud.driver.elements.service.Service;
import io.thunder.packet.PacketBuffer;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public class PacketServiceUpdate extends PacketCommunication {

    private Service service;

    @Override
    public void read(PacketBuffer buf) {
        super.read(buf);

        service = Service.readFromBuf(buf);
    }

    @Override
    public void write(PacketBuffer buf) {
        super.write(buf);

        service.writeToBuf(buf);
    }
}
