package de.lystx.hytoracloud.driver.commons.packets.in;

import de.lystx.hytoracloud.driver.commons.service.IServiceGroup;


import lombok.AllArgsConstructor;
import lombok.Getter;
import net.hytora.networking.elements.component.Component;
import net.hytora.networking.elements.packet.HytoraPacket;

import java.io.Serializable;

@Getter @AllArgsConstructor
public class PacketInStartGroup extends HytoraPacket implements Serializable {

    private IServiceGroup IServiceGroup;

    @Override
    public void write(Component component) {
        component.put("s", IServiceGroup);
    }

    @Override
    public void read(Component component) {
        IServiceGroup = (IServiceGroup) component.get("s");
    }
}
