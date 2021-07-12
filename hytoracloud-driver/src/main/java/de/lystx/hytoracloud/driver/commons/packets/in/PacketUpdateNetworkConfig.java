package de.lystx.hytoracloud.driver.commons.packets.in;

import de.lystx.hytoracloud.driver.commons.packets.both.PacketCommunication;
import de.lystx.hytoracloud.driver.cloudservices.global.config.impl.NetworkConfig;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.hytora.networking.elements.component.Component;

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
    public void read(Component component) {
        super.read(component);

        networkConfig = component.get("config");
    }

    @Override
    public void write(Component component) {
        super.write(component);


        component.put("config", networkConfig);
    }

}
