package de.lystx.hytoracloud.driver.elements.packets.in;



import lombok.AllArgsConstructor;
import lombok.Getter;
import net.hytora.networking.elements.component.Component;
import net.hytora.networking.elements.packet.HytoraPacket;

import java.io.IOException;
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
    private String player;

    @Override
    public void write(Component component) {
        component.append(map -> {
            map.put("s", service);
            map.put("p", player);
        });
    }

    @Override
    public void read(Component component) {

        service = component.get("s");
        player = component.get("p");
    }
}
