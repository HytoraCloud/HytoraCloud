package de.lystx.hytoracloud.driver.commons.packets.in;

import de.lystx.hytoracloud.driver.commons.service.IServiceGroup;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.hytora.networking.elements.component.Component;
import net.hytora.networking.elements.packet.HytoraPacket;

import java.io.Serializable;

@Getter @AllArgsConstructor
public class PacketInCreateTemplate extends HytoraPacket implements Serializable {

    private IServiceGroup IServiceGroup;
    private String template;


    @Override
    public void write(Component component) {
        component.append(map -> {
            map.put("g", IServiceGroup);
            map.put("t", template);
        });
    }

    @Override
    public void read(Component component) {

        IServiceGroup = (IServiceGroup) component.get("g");
        template = component.get("t");
    }
}
