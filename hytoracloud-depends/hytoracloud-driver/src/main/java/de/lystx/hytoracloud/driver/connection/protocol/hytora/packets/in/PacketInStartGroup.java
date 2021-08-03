package de.lystx.hytoracloud.driver.connection.protocol.hytora.packets.in;

import de.lystx.hytoracloud.driver.service.group.IServiceGroup;


import lombok.AllArgsConstructor;
import lombok.Getter;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.elements.component.Component;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.elements.packet.Packet;

@Getter @AllArgsConstructor
public class PacketInStartGroup extends Packet  {

    private IServiceGroup serviceGroup;

    @Override
    public void write(Component component) {
        component.put("s", serviceGroup);
    }

    @Override
    public void read(Component component) {
        serviceGroup = (IServiceGroup) component.get("s");
    }
}
