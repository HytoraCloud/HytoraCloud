package de.lystx.hytoracloud.driver.commons.packets.in;

import de.lystx.hytoracloud.driver.cloudservices.global.config.impl.NetworkConfig;
import lombok.AllArgsConstructor;
import lombok.Getter;
import de.lystx.hytoracloud.networking.elements.component.Component;
import de.lystx.hytoracloud.networking.elements.packet.Packet;

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
