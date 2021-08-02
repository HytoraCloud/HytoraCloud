package de.lystx.hytoracloud.driver.commons.packets.receiver;

import de.lystx.hytoracloud.driver.commons.wrapped.ReceiverObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import de.lystx.hytoracloud.networking.elements.component.Component;
import de.lystx.hytoracloud.networking.elements.packet.Packet;

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
