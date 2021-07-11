package de.lystx.hytoracloud.driver.commons.packets.in;

import de.lystx.hytoracloud.driver.commons.packets.both.PacketCommunication;
import de.lystx.hytoracloud.driver.commons.service.ServiceGroup;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.hytora.networking.elements.component.Component;

import java.io.Serializable;

@Getter @AllArgsConstructor
public class PacketInUpdateServiceGroup extends PacketCommunication implements Serializable {

    private ServiceGroup serviceGroup;


    @Override
    public void read(Component component) {
        super.read(component);

        serviceGroup = (ServiceGroup) component.get("g");
    }

    @Override
    public void write(Component component) {
        super.write(component);

        component.put("g", serviceGroup);
    }
}
