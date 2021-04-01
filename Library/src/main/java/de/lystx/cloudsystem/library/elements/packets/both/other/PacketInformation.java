package de.lystx.cloudsystem.library.elements.packets.both.other;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@Getter @AllArgsConstructor
public class PacketInformation extends PacketCommunication {

    private final String key;
    private final Map<String, Object> data;
}
