package de.lystx.cloudsystem.library.elements.packets.in.other;

import de.lystx.cloudsystem.library.service.config.impl.NetworkConfig;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class PacketInNetworkConfig extends Packet implements Serializable {

    private final NetworkConfig networkConfig;

    public PacketInNetworkConfig(NetworkConfig networkConfig) {
        this.networkConfig = networkConfig;
    }
}