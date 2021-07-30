package de.lystx.hytoracloud.driver.commons.packets.both.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import de.lystx.hytoracloud.networking.elements.component.Component;
import de.lystx.hytoracloud.networking.elements.packet.Packet;

@Getter @AllArgsConstructor
public class PacketServiceInfo extends Packet {

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
