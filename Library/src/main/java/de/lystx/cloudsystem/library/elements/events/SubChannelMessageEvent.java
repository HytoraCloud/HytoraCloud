package de.lystx.cloudsystem.library.elements.events;

import de.lystx.cloudsystem.library.elements.other.Document;
import de.lystx.cloudsystem.library.service.event.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SubChannelMessageEvent extends Event {

    private final String channel;
    private final String key;
    private final Document document;

}
