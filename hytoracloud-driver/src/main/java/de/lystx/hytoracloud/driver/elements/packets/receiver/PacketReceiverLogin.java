package de.lystx.hytoracloud.driver.elements.packets.receiver;

import de.lystx.hytoracloud.driver.elements.other.ReceiverInfo;
import io.thunder.packet.Packet;
import io.thunder.packet.PacketBuffer;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@AllArgsConstructor @Getter
public class PacketReceiverLogin extends Packet implements Serializable {

    private ReceiverInfo receiverInfo;
    private String key;


    @Override
    public void read(PacketBuffer buf) {
        receiverInfo = ReceiverInfo.fromBuf(buf);
        key = buf.readString();
    }

    @Override
    public void write(PacketBuffer buf) {
        receiverInfo.toBuf(buf);
        buf.writeString(key);
    }
}
