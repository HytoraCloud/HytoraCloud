package de.lystx.hytoracloud.driver.elements.packets.request.other;

import io.thunder.packet.Packet;
import io.thunder.packet.PacketBuffer;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public class PacketRequestKey extends Packet {

    private String key;

    @Override
    public void write(PacketBuffer buf) {
        buf.writeString(key);
    }

    @Override
    public void read(PacketBuffer buf) {
        key = buf.readString();
    }
}
