package de.lystx.hytoracloud.driver.commons.packets.in.request.other;

import lombok.AllArgsConstructor;
import lombok.Getter;
import de.lystx.hytoracloud.networking.elements.component.Component;
import de.lystx.hytoracloud.networking.elements.packet.Packet;

@Getter @AllArgsConstructor
public class PacketRequestTPS extends Packet {

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
