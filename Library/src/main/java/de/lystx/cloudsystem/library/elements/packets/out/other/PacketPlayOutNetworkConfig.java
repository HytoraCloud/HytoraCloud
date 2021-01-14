package de.lystx.cloudsystem.library.elements.packets.out.other;

import de.lystx.cloudsystem.library.service.config.impl.NetworkConfig;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class PacketPlayOutNetworkConfig extends Packet implements Serializable {

    private final NetworkConfig networkConfig;

    public PacketPlayOutNetworkConfig(NetworkConfig networkConfig) {
        super(PacketPlayOutNetworkConfig.class);
        this.networkConfig = networkConfig;
    }

}
