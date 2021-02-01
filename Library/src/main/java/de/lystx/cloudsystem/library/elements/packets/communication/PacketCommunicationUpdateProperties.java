package de.lystx.cloudsystem.library.elements.packets.communication;

import de.lystx.cloudsystem.library.elements.other.SerializableDocument;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class PacketCommunicationUpdateProperties extends PacketCommunication implements Serializable {

    private final String name;
    private final SerializableDocument document;

    public PacketCommunicationUpdateProperties(String name, SerializableDocument document) {
        this.name = name;
        this.document = document;
    }
}
