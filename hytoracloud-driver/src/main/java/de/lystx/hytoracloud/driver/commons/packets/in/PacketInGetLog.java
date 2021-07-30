package de.lystx.hytoracloud.driver.commons.packets.in;



import lombok.AllArgsConstructor;
import lombok.Getter;
import de.lystx.hytoracloud.networking.elements.component.Component;
import de.lystx.hytoracloud.networking.elements.packet.Packet;

import java.io.Serializable;

/**
 * This packet is used to handle
 * the log of a service
 * it will upload it to a pasteserver
 * and return the link of it
 */
@Getter @AllArgsConstructor
public class PacketInGetLog extends Packet implements Serializable {

    private String service;

    @Override
    public void write(Component component) {
        component.append(map -> map.put("s", service));
    }

    @Override
    public void read(Component component) {

        service = component.get("s");
    }
}
