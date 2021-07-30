package de.lystx.hytoracloud.driver.commons.packets.in;


import lombok.AllArgsConstructor;
import lombok.Getter;
import de.lystx.hytoracloud.networking.elements.component.Component;
import de.lystx.hytoracloud.networking.elements.packet.Packet;


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
