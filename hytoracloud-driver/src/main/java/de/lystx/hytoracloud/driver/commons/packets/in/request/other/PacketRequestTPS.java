package de.lystx.hytoracloud.driver.commons.packets.in.request.other;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.hytora.networking.elements.component.Component;
import net.hytora.networking.elements.packet.HytoraPacket;

@Getter @AllArgsConstructor
public class PacketRequestTPS extends HytoraPacket {

    private String server;

    @Override
    public void write(Component component) {
        component.put("server", server);
    }

    @Override
    public void read(Component component) {
        server = component.get("server");
    }
}
