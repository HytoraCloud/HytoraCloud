package de.lystx.hytoracloud.driver.elements.packets.in;

import de.lystx.hytoracloud.driver.elements.service.Service;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.hytora.networking.elements.component.Component;
import net.hytora.networking.elements.packet.HytoraPacket;

import java.io.Serializable;

@Getter @AllArgsConstructor
public class PacketInCopyTemplate extends HytoraPacket implements Serializable {

    private Service service;
    private String template;
    private String specificDirectory;


    @Override
    public void write(Component component) {

        component.append(map -> {
            map.put("s", service);
            map.put("t", template);
            map.put("sD", specificDirectory);
        });
    }

    @Override
    public void read(Component component) {

        service = (Service) component.get("s");
        template = component.get("t");
        specificDirectory = component.get("sD");
    }
}
