package de.lystx.hytoracloud.driver.commons.packets.in;

import de.lystx.hytoracloud.driver.commons.wrapped.ServiceGroupObject;

import lombok.AllArgsConstructor;
import lombok.Getter;
import de.lystx.hytoracloud.networking.elements.component.Component;
import de.lystx.hytoracloud.networking.elements.packet.Packet;

@Getter @AllArgsConstructor
public class PacketInUpdateServiceGroup extends Packet {

    private ServiceGroupObject serviceGroup;

    @Override
    public void read(Component component) {
        serviceGroup = component.get("group");
    }

    @Override
    public void write(Component component) {
        component.put("group", serviceGroup);
    }
}
