package de.lystx.hytoracloud.driver.elements.packets.out;

import de.lystx.hytoracloud.driver.elements.service.Service;


import lombok.Getter;
import lombok.Setter;
import net.hytora.networking.elements.component.Component;
import net.hytora.networking.elements.packet.HytoraPacket;

import java.io.Serializable;

@Getter @Setter
public class PacketOutRegisterServer extends HytoraPacket implements Serializable {

    private Service service;
    private String action;

    public PacketOutRegisterServer(Service service) {
        this.service = service;
    }

    public PacketOutRegisterServer setAction(String action) {
        this.action = action;
        return this;
    }

    @Override
    public void write(Component component) {
        component.put("s", service).put("a", action);
    }

    @Override
    public void read(Component component) {
        service = (Service) component.get("s");
        action = component.get("a");
    }
}
