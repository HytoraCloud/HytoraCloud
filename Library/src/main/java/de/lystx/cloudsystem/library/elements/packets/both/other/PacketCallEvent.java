package de.lystx.cloudsystem.library.elements.packets.both.other;

import de.lystx.cloudsystem.library.service.event.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@AllArgsConstructor @Getter
public class PacketCallEvent extends PacketCommunication implements Serializable {

    private final Event event;
}
