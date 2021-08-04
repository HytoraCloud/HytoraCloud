package de.lystx.hytoracloud.driver.connection.protocol.hytora.packets.receiver;

import de.lystx.hytoracloud.driver.service.receiver.IReceiver;
import de.lystx.hytoracloud.driver.service.IService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.elements.component.Component;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.elements.packet.Packet;

@AllArgsConstructor @Getter
public class PacketReceiverStartService extends Packet {

    private IReceiver receiver;
    private IService service;

    @Override
    public void write(Component component) {
        component.put("receiver", receiver);
        component.put("service", service);
    }

    @Override
    public void read(Component component) {
        receiver = component.get("receiver");
        service = component.get("service");
    }
}
