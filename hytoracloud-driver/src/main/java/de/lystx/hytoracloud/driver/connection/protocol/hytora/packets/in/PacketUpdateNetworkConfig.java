package de.lystx.hytoracloud.driver.connection.protocol.hytora.packets.in;

import de.lystx.hytoracloud.driver.config.impl.NetworkConfig;
import lombok.AllArgsConstructor;
import lombok.Getter;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.elements.component.Component;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.elements.packet.Packet;

@Getter @AllArgsConstructor
public class PacketUpdateNetworkConfig extends Packet {

    private NetworkConfig networkConfig;

    @Override
    public void read(Component component) {
        networkConfig = component.get("config");
    }

    @Override
    public void write(Component component) {
        component.put("config", networkConfig);
    }

}
