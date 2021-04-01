package de.lystx.cloudsystem.library.elements.packets.both.other;

import de.lystx.cloudsystem.library.elements.service.Service;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class PacketTPS extends PacketCommunication implements Serializable {

    private final String player;
    private final Service service;
    private final String tps;

    public PacketTPS(String player, Service service, String tps) {
        this.player = player;
        this.service = service;
        this.tps = tps;
    }
}
