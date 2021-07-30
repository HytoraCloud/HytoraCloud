package de.lystx.hytoracloud.driver.commons.packets.out;

import com.google.gson.JsonElement;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.impl.ICloudPlayer;
import de.lystx.hytoracloud.driver.commons.receiver.IReceiver;
import de.lystx.hytoracloud.driver.commons.service.IService;
import de.lystx.hytoracloud.driver.commons.service.IServiceGroup;
import de.lystx.hytoracloud.driver.cloudservices.global.config.impl.NetworkConfig;


import de.lystx.hytoracloud.driver.commons.storage.JsonDocument;
import de.lystx.hytoracloud.driver.commons.storage.JsonObject;
import de.lystx.hytoracloud.driver.commons.wrapped.ReceiverObject;
import de.lystx.hytoracloud.driver.commons.wrapped.ServiceGroupObject;
import de.lystx.hytoracloud.driver.commons.wrapped.ServiceObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import de.lystx.hytoracloud.networking.elements.component.Component;
import de.lystx.hytoracloud.networking.elements.packet.Packet;

import java.util.*;

@Getter @AllArgsConstructor
public class PacketOutGlobalInfo extends Packet {

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

        JsonObject<?> jsonObject = JsonObject.gson();

        jsonObject.append("groups", groups);
        jsonObject.append("services", services);

        component.put("json", jsonObject.toString());
        component.put("config", networkConfig);
        component.put("receivers", receivers);
        component.put("cloudPlayers", cloudPlayers);
    }

    @Override
    public void read(Component component) {

        JsonObject<?> jsonObject = JsonObject.gson((String) component.get("json"));

        this.groups = jsonObject.getInterfaceList("groups", IServiceGroup.class, ServiceGroupObject.class);
        this.services = jsonObject.getInterfaceList("services", IService.class, ServiceObject.class);

        this.networkConfig = component.get("config");
        this.receivers = component.get("receivers");
        this.cloudPlayers = component.get("cloudPlayers");
    }

}
