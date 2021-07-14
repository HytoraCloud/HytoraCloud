package de.lystx.hytoracloud.driver.commons.packets.in.request.other;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.hytora.networking.elements.component.Component;
import net.hytora.networking.elements.packet.HytoraPacket;

@Getter @AllArgsConstructor
public class PacketRequestPlayerNamed extends HytoraPacket {

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
