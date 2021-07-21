package de.lystx.hytoracloud.driver.commons.packets.in;

import de.lystx.hytoracloud.driver.commons.wrapped.ServiceGroupObject;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.hytora.networking.elements.component.Component;
import net.hytora.networking.elements.packet.HytoraPacket;

import java.io.Serializable;

@Getter @AllArgsConstructor
public class PacketInUpdateServiceGroup extends HytoraPacket implements Serializable {

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
