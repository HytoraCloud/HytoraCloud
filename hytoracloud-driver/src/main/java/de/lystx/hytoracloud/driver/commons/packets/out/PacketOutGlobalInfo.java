package de.lystx.hytoracloud.driver.commons.packets.out;

import de.lystx.hytoracloud.driver.commons.service.Service;
import de.lystx.hytoracloud.driver.commons.service.ServiceGroup;
import de.lystx.hytoracloud.driver.service.global.config.impl.NetworkConfig;



import lombok.AllArgsConstructor;
import lombok.Getter;
import net.hytora.networking.elements.component.Component;
import net.hytora.networking.elements.packet.HytoraPacket;

import java.io.Serializable;
import java.util.*;

@Getter @AllArgsConstructor
public class PacketOutGlobalInfo extends HytoraPacket implements Serializable {

    private NetworkConfig networkConfig;
    private Map<ServiceGroup, List<Service>> services;

    @Override
    public void write(Component component) {
        component.put("config", networkConfig);
        component.put("services", services);
    }

    @Override
    public void read(Component component) {

        networkConfig = component.get("config");
        services = component.get("services");

    }
}
