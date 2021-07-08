package de.lystx.hytoracloud.driver.elements.packets.out;

import de.lystx.hytoracloud.driver.elements.service.Service;
import de.lystx.hytoracloud.driver.elements.service.ServiceGroup;
import de.lystx.hytoracloud.driver.service.config.impl.NetworkConfig;
import io.thunder.packet.Packet;
import io.thunder.packet.PacketBuffer;
import io.thunder.packet.impl.JsonPacket;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import java.util.*;

@Getter @AllArgsConstructor
public class PacketOutGlobalInfo extends Packet implements Serializable {

    private NetworkConfig networkConfig;
    private Map<ServiceGroup, List<Service>> services;

    @Override
    public void write(PacketBuffer buf) {
        buf.writeThunderObject(networkConfig);

        buf.writeInt(services.size());
        for (ServiceGroup serviceGroup : services.keySet()) {
            serviceGroup.writeToBuf(buf);
            List<Service> services = this.services.get(serviceGroup);
            buf.writeInt(services.size());
            for (Service service : services) {
                service.writeToBuf(buf);
            }
        }

    }

    @Override
    public void read(PacketBuffer buf) {

        networkConfig = buf.readThunderObject(NetworkConfig.class);

        int size = buf.readInt();

        this.services = new HashMap<>(size);

        for (int i = 0; i < size; i++) {
            ServiceGroup serviceGroup = ServiceGroup.readFromBuf(buf);
            int size2 = buf.readInt();
            List<Service> services = new ArrayList<>(size2);
            for (int i1 = 0; i1 < size2; i1++) {
                services.add(Service.readFromBuf(buf));
            }
            this.services.put(serviceGroup, services);
        }
    }
}
