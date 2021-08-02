package de.lystx.hytoracloud.driver.commons.packets.receiver;

import de.lystx.hytoracloud.driver.commons.receiver.IReceiver;
import lombok.AllArgsConstructor;
import lombok.Getter;
import de.lystx.hytoracloud.networking.elements.component.Component;
import de.lystx.hytoracloud.networking.elements.packet.Packet;

@Getter @AllArgsConstructor
public class PacketReceiverUpdate extends Packet {

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
