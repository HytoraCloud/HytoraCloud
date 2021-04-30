package de.lystx.cloudapi.standalone.handler;

import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudsystem.library.elements.packets.both.other.PacketCallEvent;
import de.lystx.cloudsystem.library.service.event.Event;
import de.lystx.cloudsystem.library.service.network.packet.PacketHandler;
import lombok.Getter;

@Getter
public class PacketHandlerCallEvent {

    private final CloudAPI cloudAPI;


    public PacketHandlerCallEvent(CloudAPI cloudAPI) {
        this.cloudAPI = cloudAPI;
    }

    @PacketHandler
    public void handle(PacketCallEvent event) {
        final Event packetEvent = event.getEvent();
        CloudAPI.getInstance().getEventService().callEvent(packetEvent);
    }
}
