package de.lystx.hytoracloud.driver.connection.protocol.hytora.packets.in;


import lombok.AllArgsConstructor;
import lombok.Getter;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.elements.component.Component;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.elements.packet.Packet;


@AllArgsConstructor @Getter
public class PacketInStopServer extends Packet {


    private String service;

    @Override
    public void write(Component component) {
        component.put("service", service);
    }

    @Override
    public void read(Component component) {
        service = component.get("service");
    }
}
