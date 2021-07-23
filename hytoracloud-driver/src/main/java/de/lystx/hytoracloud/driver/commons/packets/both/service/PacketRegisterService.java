package de.lystx.hytoracloud.driver.commons.packets.both.service;

import de.lystx.hytoracloud.driver.commons.packets.both.PacketCommunication;


import de.lystx.hytoracloud.driver.commons.service.IService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.hytora.networking.elements.component.Component;
import net.hytora.networking.elements.packet.HytoraPacket;

import java.io.Serializable;

@Getter @Setter @AllArgsConstructor
public class PacketRegisterService extends HytoraPacket {

    private String service;

    private IService iService;

    public PacketRegisterService(String service) {
        this(service, null);
    }

    @Override
    public void read(Component component) {

        service = component.get("service");
        iService = component.get("iService");
    }

    @Override
    public void write(Component component) {
        component.put("iService", iService);
        component.put("service", service);
    }

}
