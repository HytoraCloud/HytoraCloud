package de.lystx.cloudsystem.library.elements.packets.communication;

import de.lystx.cloudsystem.library.service.event.raw.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor @Getter
public class PacketCallEvent extends PacketCommunication {

    private final Class<? extends Event> eventClass;
    private final Object[] parameters;
}
