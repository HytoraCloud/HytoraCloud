package de.lystx.hytoracloud.driver.commons.packets.out;

import de.lystx.hytoracloud.driver.cloudservices.managing.player.impl.ICloudPlayer;
import de.lystx.hytoracloud.driver.commons.receiver.IReceiver;
import de.lystx.hytoracloud.driver.commons.service.IService;
import de.lystx.hytoracloud.driver.commons.service.IServiceGroup;
import de.lystx.hytoracloud.driver.cloudservices.global.config.impl.NetworkConfig;


import de.lystx.hytoracloud.driver.commons.wrapped.ReceiverObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.hytora.networking.elements.component.Component;
import net.hytora.networking.elements.packet.HytoraPacket;

import java.util.*;

@Getter @AllArgsConstructor
public class PacketOutGlobalInfo extends HytoraPacket {

    private NetworkConfig networkConfig;
    private List<IServiceGroup> groups;
    private List<IService> services;
    private List<IReceiver> receivers;
    private List<ICloudPlayer> cloudPlayers;


    public PacketOutGlobalInfo(NetworkConfig networkConfig, List<IServiceGroup> groups, List<IService> services, List<ICloudPlayer> cloudPlayers) {
        this.networkConfig = networkConfig;
        this.groups = groups;
        this.services = services;
        this.cloudPlayers = cloudPlayers;
        this.receivers = new LinkedList<>();

        for (IService service : services) {
            this.receivers.add(service.getReceiver());
        }
        this.receivers.removeIf(iReceiver -> iReceiver.getClass() != ReceiverObject.class);
    }

    @Override
    public void write(Component component) {

        component.put("config", networkConfig);
        component.put("groups", groups);
        component.put("services", services);
        component.put("receivers", receivers);
        component.put("cloudPlayers", cloudPlayers);
    }

    @Override
    public void read(Component component) {

        networkConfig = component.get("config");
        services = component.get("services");
        groups = component.get("groups");
        receivers = component.get("receivers");
        cloudPlayers = component.get("cloudPlayers");
    }

}
