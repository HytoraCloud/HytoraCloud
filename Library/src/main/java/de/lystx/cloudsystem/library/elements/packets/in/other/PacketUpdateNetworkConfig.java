package de.lystx.cloudsystem.library.elements.packets.in.other;

import de.lystx.cloudsystem.library.elements.packets.both.other.PacketCommunication;
import de.lystx.cloudsystem.library.service.config.impl.NetworkConfig;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

/**
 * This class is used to update the
 * {@link NetworkConfig} all over the Network
 * its a {@link PacketCommunication} which means
 * it'll be send to every Connection
 * (CloudSystem, Modules, Spigot, Bungee, Receiver)
 * and clarifies that the {@link NetworkConfig} must
 * be updated in internal cache
 */
@Getter @AllArgsConstructor
public class PacketUpdateNetworkConfig extends PacketCommunication implements Serializable {

    private final NetworkConfig networkConfig;

}
