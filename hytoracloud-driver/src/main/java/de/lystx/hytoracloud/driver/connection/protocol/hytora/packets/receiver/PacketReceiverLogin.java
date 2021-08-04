package de.lystx.hytoracloud.driver.connection.protocol.hytora.packets.receiver;

import de.lystx.hytoracloud.driver.wrapped.ReceiverObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.elements.component.Component;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.elements.packet.Packet;

@Getter @AllArgsConstructor
public class PacketReceiverLogin extends Packet {

    private ReceiverObject receiver;
    private String key;

    @Override
    public void write(Component component) {
        component.put("receiver", receiver);
        component.put("key", key);
    }

    @Override
    public void read(Component component) {
        receiver = component.get("receiver");
        key = component.get("key");
    }
}
