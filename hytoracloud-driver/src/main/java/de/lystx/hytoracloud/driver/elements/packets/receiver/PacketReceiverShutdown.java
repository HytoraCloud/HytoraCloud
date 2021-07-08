package de.lystx.hytoracloud.driver.elements.packets.receiver;

import de.lystx.hytoracloud.driver.elements.other.ReceiverInfo;
import io.thunder.packet.Packet;
import io.thunder.packet.PacketBuffer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.hytora.networking.elements.component.Component;
import net.hytora.networking.elements.packet.HytoraPacket;

import java.io.Serializable;

@Getter @AllArgsConstructor
public class PacketReceiverShutdown extends HytoraPacket implements Serializable {

    private ReceiverInfo receiverInfo;

    @Override
    public void write(Component component) {
        component.put("r", receiverInfo);
    }

    @Override
    public void read(Component component) {
        receiverInfo = component.get("r");
    }
}
