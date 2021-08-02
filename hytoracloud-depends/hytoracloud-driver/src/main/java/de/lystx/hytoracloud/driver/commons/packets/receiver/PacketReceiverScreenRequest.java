package de.lystx.hytoracloud.driver.commons.packets.receiver;

import lombok.AllArgsConstructor;
import lombok.Getter;
import de.lystx.hytoracloud.networking.elements.component.Component;
import de.lystx.hytoracloud.networking.elements.packet.Packet;

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
