package de.lystx.hytoracloud.driver.commons.packets.receiver;

import de.lystx.hytoracloud.driver.commons.receiver.IReceiver;
import de.lystx.hytoracloud.driver.commons.service.IService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import de.lystx.hytoracloud.networking.elements.component.Component;
import de.lystx.hytoracloud.networking.elements.packet.Packet;

@AllArgsConstructor @Getter
public class PacketReceiverRegisterService extends Packet {

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
