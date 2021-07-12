package de.lystx.hytoracloud.driver.commons.packets.out;

import de.lystx.hytoracloud.driver.commons.service.IService;
import de.lystx.hytoracloud.driver.commons.service.IServiceGroup;
import de.lystx.hytoracloud.driver.cloudservices.global.config.impl.NetworkConfig;



import lombok.AllArgsConstructor;
import lombok.Getter;
import net.hytora.networking.elements.component.Component;
import net.hytora.networking.elements.packet.HytoraPacket;

import java.io.Serializable;
import java.util.*;

@Getter @AllArgsConstructor
public class PacketOutGlobalInfo extends HytoraPacket implements Serializable {

    private NetworkConfig networkConfig;
    private List<IServiceGroup> groups;
    private List<IService> services;

    @Override
    public void write(Component component) {
        component.put("config", networkConfig);
        component.put("groups", groups);
        component.put("services", services);
    }

    @Override
    public void read(Component component) {

        networkConfig = component.get("config");
        services = component.get("services");
        groups = component.get("groups");

    }


    public Map<IServiceGroup, List<IService>> toMap() {
        Map<IServiceGroup, List<IService>> map = new HashMap<>();

        for (IServiceGroup group : this.groups) {
            List<IService> list = new LinkedList<>();
            for (IService service : this.services) {
                if (service.getGroup().getName().equalsIgnoreCase(group.getName())) {
                    list.add(service);
                }
            }
            map.put(group, list);
        }

        return map;
    }
}
