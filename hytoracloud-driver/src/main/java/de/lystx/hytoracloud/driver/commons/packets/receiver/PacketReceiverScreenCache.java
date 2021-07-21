package de.lystx.hytoracloud.driver.commons.packets.receiver;

import de.lystx.hytoracloud.driver.commons.receiver.IReceiver;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.hytora.networking.elements.component.Component;
import net.hytora.networking.elements.packet.HytoraPacket;

@Getter @AllArgsConstructor
public class PacketReceiverScreenCache extends HytoraPacket {

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
