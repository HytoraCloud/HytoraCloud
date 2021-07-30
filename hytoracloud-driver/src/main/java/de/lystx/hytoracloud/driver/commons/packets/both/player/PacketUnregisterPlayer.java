package de.lystx.hytoracloud.driver.commons.packets.both.player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import de.lystx.hytoracloud.networking.elements.component.Component;
import de.lystx.hytoracloud.networking.elements.packet.Packet;

@Getter @AllArgsConstructor
public class PacketUnregisterPlayer extends Packet {

    private String name;

    @Override
    public void read(Component component) {
        name = component.get("name");
    }

    @Override
    public void write(Component component) {
        component.append(map -> map.put("name", name));
    }

}
