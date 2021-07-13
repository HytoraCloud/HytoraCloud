package de.lystx.hytoracloud.driver.commons.packets.in;



import lombok.AllArgsConstructor;
import lombok.Getter;
import net.hytora.networking.elements.component.Component;
import net.hytora.networking.elements.packet.HytoraPacket;

import java.io.Serializable;

/**
 * This packet is used to handle
 * the log of a service
 * it will upload it to a pasteserver
 * and return the link of it
 */
@Getter @AllArgsConstructor
public class PacketInGetLog extends HytoraPacket implements Serializable {

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
