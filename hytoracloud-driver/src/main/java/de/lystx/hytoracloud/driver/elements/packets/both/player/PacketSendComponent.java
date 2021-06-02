package de.lystx.hytoracloud.driver.elements.packets.both.player;

import de.lystx.hytoracloud.driver.elements.chat.CloudComponent;
import de.lystx.hytoracloud.driver.elements.packets.both.PacketCommunication;
import io.thunder.packet.PacketBuffer;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter  @AllArgsConstructor
public class PacketSendComponent extends PacketCommunication {

    private UUID uuid;
    private CloudComponent cloudComponent;


    @Override
    public void read(PacketBuffer buf) {
        super.read(buf);

        buf.nullSafe().writeUUID(uuid);
        buf.writeThunderObject(cloudComponent);
    }


    @Override
    public void write(PacketBuffer buf) {
        super.write(buf);

        uuid = buf.nullSafe().readUUID();
        cloudComponent = buf.readThunderObject(CloudComponent.class);
    }
}
