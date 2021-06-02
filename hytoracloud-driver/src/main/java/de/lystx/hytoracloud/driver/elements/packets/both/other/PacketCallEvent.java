package de.lystx.hytoracloud.driver.elements.packets.both.other;

import de.lystx.hytoracloud.driver.elements.other.JsonBuilder;
import de.lystx.hytoracloud.driver.elements.packets.both.PacketCommunication;
import de.lystx.hytoracloud.driver.service.event.CloudEvent;
import io.thunder.packet.PacketBuffer;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public class PacketCallEvent extends PacketCommunication {

    private CloudEvent cloudEvent;

    @Override
    public void read(PacketBuffer buf) {
        super.read(buf);

        cloudEvent = JsonBuilder.fromClass(buf.readString(), CloudEvent.class);
    }


    @Override
    public void write(PacketBuffer buf) {
        super.write(buf);

        buf.writeString(JsonBuilder.toString(cloudEvent));
    }

}
