package de.lystx.hytoracloud.driver.commons.packets.receiver;

import de.lystx.hytoracloud.driver.commons.implementations.ReceiverObject;
import de.lystx.hytoracloud.driver.commons.receiver.IReceiver;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.hytora.networking.elements.component.Component;
import net.hytora.networking.elements.packet.HytoraPacket;

@Getter @AllArgsConstructor
public class PacketReceiverScreenRequest extends HytoraPacket {

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
