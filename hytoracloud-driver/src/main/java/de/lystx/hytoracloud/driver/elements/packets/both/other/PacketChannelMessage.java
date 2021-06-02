package de.lystx.hytoracloud.driver.elements.packets.both.other;

import de.lystx.hytoracloud.driver.elements.packets.both.PacketCommunication;
import de.lystx.hytoracloud.driver.service.messenger.ChannelMessage;
import io.thunder.packet.PacketBuffer;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PacketChannelMessage extends PacketCommunication {

    private ChannelMessage channelMessage;

    @Override
    public void write(PacketBuffer buf) {
        super.write(buf);
        buf.writeThunderObject(channelMessage);
    }

    @Override
    public void read(PacketBuffer buf) {
        super.read(buf);
        channelMessage = buf.readThunderObject(ChannelMessage.class);
    }
}
