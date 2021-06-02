package de.lystx.hytoracloud.driver.elements.packets.both.player;

import de.lystx.hytoracloud.driver.elements.packets.both.PacketCommunication;
import io.thunder.packet.PacketBuffer;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter @AllArgsConstructor
public class PacketSendActionbar extends PacketCommunication {

    private UUID uuid;
    private String message;


    @Override
    public void read(PacketBuffer buf) {
        super.read(buf);

        uuid = buf.readUUID();
        message = buf.readString();
    }

    @Override
    public void write(PacketBuffer buf) {
        super.write(buf);

        buf.writeUUID(uuid);
        buf.writeString(message);
    }
}
