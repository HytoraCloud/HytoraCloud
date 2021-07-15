package de.lystx.hytoracloud.driver.commons.packets.in;

import de.lystx.hytoracloud.driver.commons.service.IService;


import lombok.AllArgsConstructor;
import lombok.Getter;
import net.hytora.networking.elements.component.Component;
import net.hytora.networking.elements.packet.HytoraPacket;


@AllArgsConstructor @Getter
public class PacketInStopServer extends HytoraPacket  {


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
