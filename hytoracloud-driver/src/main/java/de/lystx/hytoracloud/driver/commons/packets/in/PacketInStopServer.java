package de.lystx.hytoracloud.driver.commons.packets.in;

import de.lystx.hytoracloud.driver.commons.service.Service;


import lombok.AllArgsConstructor;
import lombok.Getter;
import net.hytora.networking.elements.component.Component;
import net.hytora.networking.elements.packet.HytoraPacket;

import java.io.Serializable;


@AllArgsConstructor @Getter
public class PacketInStopServer extends HytoraPacket implements Serializable {

    private Service service;

    @Override
    public void write(Component component) {
        component.put("s", service);
    }

    @Override
    public void read(Component component) {
        service = (Service) component.get("s");
    }
}
