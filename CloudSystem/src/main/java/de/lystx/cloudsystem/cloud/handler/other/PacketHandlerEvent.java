package de.lystx.cloudsystem.cloud.handler.other;

import de.lystx.cloudsystem.cloud.CloudSystem;
import de.lystx.cloudsystem.library.elements.packets.communication.PacketCallEvent;
import de.lystx.cloudsystem.library.service.event.EventService;
import de.lystx.cloudsystem.library.service.event.raw.Event;
import de.lystx.cloudsystem.library.service.network.packet.raw.PacketHandler;
import lombok.AllArgsConstructor;

import java.lang.reflect.InvocationTargetException;

@AllArgsConstructor
public class PacketHandlerEvent {

    private final CloudSystem cloudSystem;

    @PacketHandler
    public void handleEvent(PacketCallEvent packet) {
        try {
            Event event = (Event) packet.getEventClass().getDeclaredConstructors()[0].newInstance(packet.getParameters());
            this.cloudSystem.getService(EventService.class).callEvent(event);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
