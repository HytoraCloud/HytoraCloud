package de.lystx.hytoracloud.driver.commons.packets.in;

import de.lystx.hytoracloud.driver.commons.service.IServiceGroup;


import lombok.AllArgsConstructor;
import lombok.Getter;
import de.lystx.hytoracloud.networking.elements.component.Component;
import de.lystx.hytoracloud.networking.elements.packet.Packet;

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
