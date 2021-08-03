package de.lystx.hytoracloud.driver.connection.protocol.hytora.packets.both;

import de.lystx.hytoracloud.driver.service.IService;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.elements.component.Component;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.elements.packet.Packet;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public class PacketReloadService extends Packet {

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

