package de.lystx.hytoracloud.driver.elements.packets.both.other;

import de.lystx.hytoracloud.driver.elements.packets.both.PacketCommunication;
import de.lystx.hytoracloud.driver.service.messenger.ChannelMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.hytora.networking.elements.component.Component;

@AllArgsConstructor
@Getter
public class PacketChannelMessage extends PacketCommunication {

    private ChannelMessage channelMessage;

    @Override
    public void write(Component component) {
        super.write(component);

        component.append(map -> map.put("message", channelMessage));
    }

    @Override
    public void read(Component component) {
        super.read(component);

        channelMessage = (ChannelMessage) component.get("message");
    }

}
