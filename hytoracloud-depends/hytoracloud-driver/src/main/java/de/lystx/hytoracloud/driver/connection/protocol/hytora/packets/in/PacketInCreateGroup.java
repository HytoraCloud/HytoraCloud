package de.lystx.hytoracloud.driver.connection.protocol.hytora.packets.in;

import de.lystx.hytoracloud.driver.service.group.IServiceGroup;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.elements.component.Component;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.elements.packet.Packet;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public class PacketInCreateGroup extends Packet {

    private IServiceGroup serviceGroup;


    @Override
    public void write(Component component) {
        component.put("serviceGroup", serviceGroup);
    }

    @Override
    public void read(Component component) {

        serviceGroup = component.get("serviceGroup");
    }
}
