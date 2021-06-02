package de.lystx.hytoracloud.driver.elements.packets.in;

import de.lystx.hytoracloud.driver.elements.packets.both.PacketCommunication;
import de.lystx.hytoracloud.driver.service.config.impl.NetworkConfig;
import io.thunder.packet.PacketBuffer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;

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

    private NetworkConfig networkConfig;

    @Override
    public void read(PacketBuffer buf) {
        super.read(buf);

        buf.writeThunderObject(networkConfig);
    }

    @Override @SneakyThrows
    public void write(PacketBuffer buf) {
        super.write(buf);

        networkConfig = buf.readThunderObject(NetworkConfig.class);
    }
}
