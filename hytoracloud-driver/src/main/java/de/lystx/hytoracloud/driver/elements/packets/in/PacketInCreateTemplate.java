package de.lystx.hytoracloud.driver.elements.packets.in;

import de.lystx.hytoracloud.driver.elements.service.ServiceGroup;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.hytora.networking.elements.component.Component;
import net.hytora.networking.elements.packet.HytoraPacket;

import java.io.Serializable;

@Getter @AllArgsConstructor
public class PacketInCreateTemplate extends HytoraPacket implements Serializable {

    private ServiceGroup serviceGroup;
    private String template;


    @Override
    public void write(Component component) {
        component.append(map -> {
            map.put("g", serviceGroup);
            map.put("t", template);
        });
    }

    @Override
    public void read(Component component) {

        serviceGroup = (ServiceGroup) component.get("g");
        template = component.get("t");
    }
}
