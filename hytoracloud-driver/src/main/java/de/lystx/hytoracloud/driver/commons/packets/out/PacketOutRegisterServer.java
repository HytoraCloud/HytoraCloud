package de.lystx.hytoracloud.driver.commons.packets.out;

import de.lystx.hytoracloud.driver.commons.service.IService;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.hytora.networking.elements.component.Component;
import net.hytora.networking.elements.packet.HytoraPacket;

import java.io.Serializable;

@Getter @Setter @AllArgsConstructor
public class PacketOutRegisterServer extends HytoraPacket {

    private IService service;

    @Override
    public void write(Component component) {
        component.put("s", service);
    }

    @Override
    public void read(Component component) {
        service = component.get("s");
    }
}
