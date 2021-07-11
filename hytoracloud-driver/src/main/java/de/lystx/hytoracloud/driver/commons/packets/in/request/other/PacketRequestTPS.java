package de.lystx.hytoracloud.driver.commons.packets.in.request.other;

import de.lystx.hytoracloud.driver.commons.packets.both.PacketCommunication;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.hytora.networking.elements.component.Component;
import net.hytora.networking.elements.packet.HytoraPacket;

@Getter @AllArgsConstructor
public class PacketRequestTPS extends PacketCommunication {

    private String server;

    @Override
    public void write(Component component) {
        super.write(component);
        component.put("server", server);
    }

    @Override
    public void read(Component component) {
        super.read(component);
        server = component.get("server");
    }
}
