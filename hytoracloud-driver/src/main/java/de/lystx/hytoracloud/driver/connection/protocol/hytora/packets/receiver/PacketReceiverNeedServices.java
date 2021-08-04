package de.lystx.hytoracloud.driver.connection.protocol.hytora.packets.receiver;

import de.lystx.hytoracloud.driver.service.receiver.IReceiver;
import de.lystx.hytoracloud.driver.service.group.IServiceGroup;
import lombok.AllArgsConstructor;
import lombok.Getter;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.elements.component.Component;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.elements.packet.Packet;

@AllArgsConstructor @Getter
public class PacketReceiverNeedServices extends Packet {

    private IReceiver receiver;
    private IServiceGroup serviceGroup;

    @Override
    public void write(Component component) {
        component.put("receiver", receiver);
        component.put("serviceGroup", serviceGroup);
    }

    @Override
    public void read(Component component) {
        receiver = component.get("receiver");
        serviceGroup = component.get("serviceGroup");
    }
}
