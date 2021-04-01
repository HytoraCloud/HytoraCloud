package de.lystx.cloudsystem.cloud.handler.other;

import de.lystx.cloudsystem.cloud.CloudSystem;
import de.lystx.cloudsystem.library.elements.packets.both.other.PacketCallEvent;
import de.lystx.cloudsystem.library.service.event.EventService;
import de.lystx.cloudsystem.library.service.network.packet.PacketHandler;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class
PacketHandlerEvent {

    private final CloudSystem cloudSystem;

    @PacketHandler
    public void handleEvent(PacketCallEvent packet) {
        this.cloudSystem.getService(EventService.class).callEvent(packet.getEvent());
    }
}
