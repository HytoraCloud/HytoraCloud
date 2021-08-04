package de.lystx.hytoracloud.driver.connection.protocol.hytora.packets.both.service;

import de.lystx.hytoracloud.driver.connection.protocol.hytora.packets.both.PacketCommunication;
import de.lystx.hytoracloud.driver.service.IService;

import lombok.AllArgsConstructor;
import lombok.Getter;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.elements.component.Component;

@Getter @AllArgsConstructor
public class PacketServiceUpdate extends PacketCommunication {

    private IService service;

    @Override
    public void read(Component component) {
        super.read(component);

        service = component.get("service");
    }

    @Override
    public void write(Component component) {
        super.write(component);

        component.append(map -> map.put("service", service));
    }

}
