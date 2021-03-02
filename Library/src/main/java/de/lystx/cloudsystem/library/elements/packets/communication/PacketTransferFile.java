package de.lystx.cloudsystem.library.elements.packets.communication;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.File;
import java.io.Serializable;

@Getter @AllArgsConstructor
public class PacketTransferFile extends PacketCommunication implements Serializable {

    private final String key;
    private final File file;

}
