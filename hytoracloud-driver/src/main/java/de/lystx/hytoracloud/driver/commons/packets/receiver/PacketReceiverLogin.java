package de.lystx.hytoracloud.driver.commons.packets.receiver;

import de.lystx.hytoracloud.driver.commons.implementations.ReceiverObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.hytora.networking.elements.component.Component;
import net.hytora.networking.elements.packet.HytoraPacket;

@Getter @AllArgsConstructor
public class PacketReceiverLogin extends HytoraPacket {

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
