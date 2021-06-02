package de.lystx.hytoracloud.driver.elements.packets.receiver;

import de.lystx.hytoracloud.driver.elements.other.ReceiverInfo;
import io.thunder.packet.Packet;
import io.thunder.packet.PacketBuffer;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter @AllArgsConstructor
public class PacketReceiverShutdown extends Packet implements Serializable {

    private ReceiverInfo receiverInfo;

    @Override
    public void read(PacketBuffer buf) {
        receiverInfo = ReceiverInfo.fromBuf(buf);
    }

    @Override
    public void write(PacketBuffer buf) {
        receiverInfo.toBuf(buf);
    }
}
