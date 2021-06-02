package de.lystx.hytoracloud.driver.elements.packets.both.player;

import de.lystx.hytoracloud.driver.elements.packets.both.PacketCommunication;
import io.thunder.packet.PacketBuffer;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter @AllArgsConstructor
public class PacketConnectGroup extends PacketCommunication implements Serializable {

    private String name;
    private String group;


    @Override
    public void read(PacketBuffer buf) {
        super.read(buf);

        name = buf.readString();
        group = buf.readString();
    }

    @Override
    public void write(PacketBuffer buf) {
        super.write(buf);

        buf.writeString(name);
        buf.writeString(group);
    }
}
