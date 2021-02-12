package de.lystx.cloudsystem.handler.other;

import de.lystx.cloudsystem.CloudSystem;
import de.lystx.cloudsystem.library.elements.events.SubChannelMessageEvent;
import de.lystx.cloudsystem.library.elements.packets.communication.PacketCommunicationSubMessage;
import de.lystx.cloudsystem.library.elements.service.ServiceType;
import de.lystx.cloudsystem.library.service.event.EventService;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import lombok.AllArgsConstructor;
import de.lystx.cloudsystem.library.service.network.connection.adapter.PacketHandlerAdapter;

@AllArgsConstructor
public class PacketHandlerSubChannel extends PacketHandlerAdapter {

    private final CloudSystem cloudSystem;

    @Override
    public void handle(Packet packet) {
        if (packet instanceof PacketCommunicationSubMessage) {
            PacketCommunicationSubMessage subMessage = (PacketCommunicationSubMessage)packet;
            if (!subMessage.getType().equals(ServiceType.CLOUDSYSTEM)) {
                return;
            }
            this.cloudSystem.getService(EventService.class).callEvent(new SubChannelMessageEvent(subMessage.getChannel(), subMessage.getChannel(), subMessage.getDocument()));
        }
    }
}
