package de.lystx.hytoracloud.driver.elements.packets.out;


import io.thunder.packet.Packet;
import io.thunder.packet.PacketBuffer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.hytora.networking.elements.component.Component;
import net.hytora.networking.elements.packet.HytoraPacket;

import java.io.IOException;
import java.io.Serializable;

@Getter @AllArgsConstructor
public class PacketOutStartedServer extends HytoraPacket implements Serializable {

    private String service;

    @Override
    public void write(Component component) {
        component.put("s", service);
    }

    @Override
    public void read(Component component) {
        service = component.getString("s");
    }
}
