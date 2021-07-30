package de.lystx.hytoracloud.driver.commons.packets.out;

import de.lystx.hytoracloud.driver.commons.service.IService;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import de.lystx.hytoracloud.networking.elements.component.Component;
import de.lystx.hytoracloud.networking.elements.packet.Packet;

@Getter @Setter @AllArgsConstructor
public class PacketOutRegisterServer extends Packet {

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
