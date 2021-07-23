package de.lystx.hytoracloud.driver.commons.packets.receiver;

import de.lystx.hytoracloud.driver.commons.receiver.IReceiver;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.hytora.networking.elements.component.Component;
import net.hytora.networking.elements.packet.HytoraPacket;

@Getter @AllArgsConstructor
public class PacketReceiverUpdate extends HytoraPacket {

    private IReceiver receiver;

    @Override
    public void write(Component component) {
        component.put("receiver", receiver);
    }

    @Override
    public void read(Component component) {
        receiver = component.get("receiver");
    }
}
