package de.lystx.hytoracloud.driver.commons.packets.in.request.other;

import lombok.AllArgsConstructor;
import lombok.Getter;
import de.lystx.hytoracloud.networking.elements.component.Component;
import de.lystx.hytoracloud.networking.elements.packet.Packet;

@Getter @AllArgsConstructor
public class PacketRequestPlayerNamed extends Packet {

    private String name;

    @Override
    public void write(Component component) {
        component.put("name", name);
    }

    @Override
    public void read(Component component) {
        name = component.get("name");
    }
}
