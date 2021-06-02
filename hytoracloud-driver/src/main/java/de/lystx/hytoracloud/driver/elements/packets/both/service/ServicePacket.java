package de.lystx.hytoracloud.driver.elements.packets.both.service;

import de.lystx.hytoracloud.driver.elements.packets.both.PacketCommunication;
import io.thunder.packet.Packet;
import io.thunder.packet.PacketBuffer;
import io.thunder.utils.ThunderUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;

@Getter @AllArgsConstructor
public class ServicePacket extends PacketCommunication {

    private String service;
    private Packet packet;

    @Override
    public void write(PacketBuffer buf) {
        super.write(buf);

        buf.writeString(service);
        buf.writeString(packet.getClass().getName());
        packet.write(buf);
    }

    @SneakyThrows
    @Override
    public void read(PacketBuffer buf) {
        super.read(buf);

        service = buf.readString();

        Class<? extends Packet> packetClass = (Class<? extends Packet>) Class.forName(buf.readString());
        packet = ThunderUtils.getInstance(packetClass);
        packet.read(buf);
    }
}
