package de.lystx.cloudsystem.library.elements.packets.communication;

import de.lystx.cloudsystem.library.elements.service.Service;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class PacketPlayOutTPS extends PacketCommunication implements Serializable {

    private final String player;
    private final Service service;
    private final String tps;

    public PacketPlayOutTPS(String player, Service service, String tps) {
        super(PacketPlayOutTPS.class);
        this.player = player;
        this.service = service;
        this.tps = tps;
    }
}
