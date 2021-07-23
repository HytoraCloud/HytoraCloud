package de.lystx.hytoracloud.driver.commons.packets.receiver;

import de.lystx.hytoracloud.driver.commons.receiver.IReceiver;
import de.lystx.hytoracloud.driver.commons.service.IService;
import de.lystx.hytoracloud.driver.commons.service.IServiceGroup;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.hytora.networking.elements.component.Component;
import net.hytora.networking.elements.packet.HytoraPacket;

@AllArgsConstructor @Getter
public class PacketReceiverNeedServices extends HytoraPacket {

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
