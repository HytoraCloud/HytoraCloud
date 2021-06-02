package de.lystx.hytoracloud.driver.elements.packets.both.player;

import de.lystx.hytoracloud.driver.elements.packets.both.PacketCommunication;
import io.thunder.packet.PacketBuffer;
import lombok.AllArgsConstructor;
import lombok.Getter;



@Getter  @AllArgsConstructor
public class PacketKickPlayer extends PacketCommunication {

    private String name;
    private String reason;


    @Override
    public void read(PacketBuffer buf) {
        super.read(buf);

        name = buf.readString();
        reason = buf.readString();
    }

    @Override
    public void write(PacketBuffer buf) {
        super.write(buf);

        buf.writeString(name);
        buf.writeString(reason);
    }
}
