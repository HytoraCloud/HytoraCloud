package de.lystx.hytoracloud.driver.connection.protocol.hytora.packets.both.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.elements.component.Component;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.elements.packet.Packet;

@Getter @AllArgsConstructor
public class PacketServiceMinecraftInfo extends Packet {

    private String service;

    @Override
    public void read(Component component) {

        service = component.get("service");
    }

    @Override
    public void write(Component component) {

        component.put("service", service);
    }
}
