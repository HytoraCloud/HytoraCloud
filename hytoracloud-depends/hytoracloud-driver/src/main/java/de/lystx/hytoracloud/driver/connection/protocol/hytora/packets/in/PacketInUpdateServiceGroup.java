package de.lystx.hytoracloud.driver.connection.protocol.hytora.packets.in;

import de.lystx.hytoracloud.driver.wrapped.GroupObject;

import lombok.AllArgsConstructor;
import lombok.Getter;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.elements.component.Component;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.elements.packet.Packet;

@Getter @AllArgsConstructor
public class PacketInUpdateServiceGroup extends Packet {

    private GroupObject serviceGroup;

    @Override
    public void read(Component component) {
        serviceGroup = component.get("group");
    }

    @Override
    public void write(Component component) {
        component.put("group", serviceGroup);
    }
}
