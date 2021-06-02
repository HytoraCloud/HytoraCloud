package de.lystx.hytoracloud.driver.elements.packets.both.other;

import de.lystx.hytoracloud.driver.elements.packets.both.PacketCommunication;
import de.lystx.hytoracloud.driver.elements.service.Service;
import io.thunder.packet.PacketBuffer;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter @AllArgsConstructor
public class PacketTPS extends PacketCommunication implements Serializable {

    private String player;
    private Service service;
    private String tps;

    @Override
    public void read(PacketBuffer buf) {
        super.read(buf);

        player = buf.readString();
        service = Service.readFromBuf(buf);
        tps = buf.readString();
    }

    @Override
    public void write(PacketBuffer buf) {
        super.write(buf);

        buf.writeString(player);
        service.writeToBuf(buf);
        buf.writeString(tps);
    }
}
