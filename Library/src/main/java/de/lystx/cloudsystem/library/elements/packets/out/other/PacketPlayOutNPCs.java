package de.lystx.cloudsystem.library.elements.packets.out.other;

import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;

import java.io.Serializable;

public class PacketPlayOutNPCs extends Packet implements Serializable {

    private final String config;

    public PacketPlayOutNPCs(String config) {
        super(PacketPlayOutNPCs.class);
        this.config = config;
    }


    public String getConfig() {
        return config;
    }
}
