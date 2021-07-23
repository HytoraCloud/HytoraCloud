package de.lystx.hytoracloud.driver.commons.packets.receiver;

import de.lystx.hytoracloud.driver.commons.receiver.IReceiver;
import de.lystx.hytoracloud.driver.commons.service.IService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.hytora.networking.elements.component.Component;
import net.hytora.networking.elements.packet.HytoraPacket;

@AllArgsConstructor @Getter
public class PacketReceiverStopService extends HytoraPacket {

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
