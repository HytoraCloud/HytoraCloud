package de.lystx.hytoracloud.driver.connection.protocol.hytora.packets.receiver;

import lombok.AllArgsConstructor;
import lombok.Getter;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.elements.component.Component;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.elements.packet.Packet;

@Getter @AllArgsConstructor
public class PacketReceiverScreenRequest extends Packet {

    private String screen;

    @Override
    public void write(Component component) {
        component.put("screen", screen);
    }

    @Override
    public void read(Component component) {
        screen = component.get("screen");
    }
}
