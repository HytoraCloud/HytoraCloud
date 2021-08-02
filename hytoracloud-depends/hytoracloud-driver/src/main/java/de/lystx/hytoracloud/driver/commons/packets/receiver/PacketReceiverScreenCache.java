package de.lystx.hytoracloud.driver.commons.packets.receiver;

import lombok.AllArgsConstructor;
import lombok.Getter;
import de.lystx.hytoracloud.networking.elements.component.Component;
import de.lystx.hytoracloud.networking.elements.packet.Packet;

@Getter @AllArgsConstructor
public class PacketReceiverScreenCache extends Packet {

    private String screen;
    private String line;

    @Override
    public void write(Component component) {
        component.put("line", line);
        component.put("screen", screen);
    }

    @Override
    public void read(Component component) {
        line = component.get("line");
        screen = component.get("screen");
    }
}
