package de.lystx.hytoracloud.driver.connection.protocol.hytora.packets.both.player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.elements.component.Component;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.elements.packet.Packet;

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
