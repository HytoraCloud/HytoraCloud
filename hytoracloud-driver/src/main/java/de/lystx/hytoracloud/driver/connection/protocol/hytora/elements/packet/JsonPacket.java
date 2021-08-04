package de.lystx.hytoracloud.driver.connection.protocol.hytora.elements.packet;

import de.lystx.hytoracloud.driver.connection.protocol.hytora.elements.component.Component;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class JsonPacket<V extends JsonPacket<V>> extends Packet {

    @Override
    public void write(Component component) {

    }

    @Override
    public void read(Component component) {

    }
}
