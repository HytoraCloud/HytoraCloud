package de.lystx.hytoracloud.driver.commons.packets.both.service;

import de.lystx.hytoracloud.driver.commons.packets.both.PacketCommunication;
import de.lystx.hytoracloud.driver.commons.service.Service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.hytora.networking.elements.component.Component;

@Getter @AllArgsConstructor
public class PacketServiceUpdate extends PacketCommunication {

    private Service service;

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
