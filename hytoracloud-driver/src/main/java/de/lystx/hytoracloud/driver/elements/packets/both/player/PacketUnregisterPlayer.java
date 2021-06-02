package de.lystx.hytoracloud.driver.elements.packets.both.player;

import de.lystx.hytoracloud.driver.elements.packets.both.PacketCommunication;
import io.thunder.packet.PacketBuffer;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter @AllArgsConstructor
public class PacketUnregisterPlayer extends PacketCommunication implements Serializable {

    private String name;

    @Override
    public void read(PacketBuffer buf) {
        super.read(buf);

        name = buf.readString();
    }


    @Override
    public void write(PacketBuffer buf) {
        super.write(buf);

        buf.writeString(name);
    }
}
