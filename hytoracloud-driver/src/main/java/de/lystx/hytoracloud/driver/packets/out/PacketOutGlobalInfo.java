package de.lystx.hytoracloud.driver.packets.out;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.connection.protocol.netty.global.packet.impl.json.JsonPacket;
import de.lystx.hytoracloud.driver.connection.protocol.netty.global.packet.impl.json.PacketSerializable;
import de.lystx.hytoracloud.driver.player.ICloudPlayer;
import de.lystx.hytoracloud.driver.service.receiver.IReceiver;
import de.lystx.hytoracloud.driver.service.IService;
import de.lystx.hytoracloud.driver.service.group.IServiceGroup;
import de.lystx.hytoracloud.driver.config.impl.NetworkConfig;


import de.lystx.hytoracloud.driver.wrapped.PlayerObject;
import de.lystx.hytoracloud.driver.wrapped.ReceiverObject;
import de.lystx.hytoracloud.driver.wrapped.GroupObject;
import de.lystx.hytoracloud.driver.wrapped.ServiceObject;
import lombok.AllArgsConstructor;
import lombok.Getter;


import lombok.ToString;

import java.util.*;

@Getter @AllArgsConstructor @ToString
public class PacketOutGlobalInfo extends JsonPacket {

    @PacketSerializable
    private NetworkConfig networkConfig;

    @PacketSerializable(GroupObject.class)
    private List<IServiceGroup> groups;

    @PacketSerializable(ServiceObject.class)
    private List<IService> services;

    @PacketSerializable(ReceiverObject.class)
    private List<IReceiver> receivers;

    @PacketSerializable(PlayerObject.class)
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

    public static PacketOutGlobalInfo create() {
        return (new PacketOutGlobalInfo(
                CloudDriver.getInstance().getConfigManager().getNetworkConfig(),
                CloudDriver.getInstance().getGroupManager().getCachedObjects(),
                CloudDriver.getInstance().getServiceManager().getCachedObjects(),
                CloudDriver.getInstance().getPlayerManager().getCachedObjects()
        ));
    }

}
