package de.lystx.hytoracloud.driver.elements.packets.in;

import de.lystx.hytoracloud.driver.elements.service.ServiceGroup;
import io.thunder.packet.Packet;
import io.thunder.packet.PacketBuffer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.hytora.networking.elements.component.Component;
import net.hytora.networking.elements.packet.HytoraPacket;

import java.io.Serializable;

@Getter @AllArgsConstructor
public class PacketInStartGroup extends HytoraPacket implements Serializable {

    private ServiceGroup serviceGroup;

    @Override
    public void write(Component component) {
        component.put("s", serviceGroup);
    }

    @Override
    public void read(Component component) {
        serviceGroup = (ServiceGroup) component.get("s");
    }
}
