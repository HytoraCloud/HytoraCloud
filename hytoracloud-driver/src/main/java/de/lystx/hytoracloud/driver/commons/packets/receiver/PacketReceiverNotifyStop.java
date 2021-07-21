package de.lystx.hytoracloud.driver.commons.packets.receiver;

import de.lystx.hytoracloud.driver.commons.service.IService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.hytora.networking.elements.component.Component;
import net.hytora.networking.elements.packet.HytoraPacket;

@Getter @AllArgsConstructor
public class PacketReceiverNotifyStop extends HytoraPacket {

    private IService service;

    @Override
    public void write(Component component) {
        component.put("service", service);
    }

    @Override
    public void read(Component component) {
        service = component.get("service");
    }
}
