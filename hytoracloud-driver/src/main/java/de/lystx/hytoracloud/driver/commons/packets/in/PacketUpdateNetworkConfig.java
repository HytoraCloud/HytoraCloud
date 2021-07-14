package de.lystx.hytoracloud.driver.commons.packets.in;

import de.lystx.hytoracloud.driver.commons.packets.both.PacketCommunication;
import de.lystx.hytoracloud.driver.cloudservices.global.config.impl.NetworkConfig;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.hytora.networking.elements.component.Component;
import net.hytora.networking.elements.packet.HytoraPacket;

import java.io.Serializable;

@Getter @AllArgsConstructor
public class PacketUpdateNetworkConfig extends HytoraPacket implements Serializable {

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
