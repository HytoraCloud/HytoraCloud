package de.lystx.hytoracloud.driver.elements.packets.out;



import lombok.AllArgsConstructor;
import lombok.Getter;
import net.hytora.networking.elements.component.Component;
import net.hytora.networking.elements.packet.HytoraPacket;

import java.io.IOException;
import java.io.Serializable;

@Getter @AllArgsConstructor
public class PacketOutStopServer extends HytoraPacket implements Serializable {

    private String service;

    @Override
    public void write(Component component) {
        component.put("s", service);
    }

    @Override
    public void read(Component component) {
        service = component.get("s");
    }
}
