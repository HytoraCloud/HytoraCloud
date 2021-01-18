package de.lystx.cloudapi.standalone.handler;

import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudsystem.library.elements.packets.out.service.PacketPlayOutServices;
import de.lystx.cloudsystem.library.elements.service.Service;
import de.lystx.cloudsystem.library.elements.service.ServiceType;
import de.lystx.cloudsystem.library.service.network.connection.adapter.PacketHandlerAdapter;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import lombok.Getter;

import java.util.List;

@Getter
public class PacketHandlerServices extends PacketHandlerAdapter {


    private final CloudAPI cloudAPI;

    public PacketHandlerServices(CloudAPI cloudAPI) {
        this.cloudAPI = cloudAPI;
    }

    @Override
    public void handle(Packet packet) {
        if (packet instanceof PacketPlayOutServices) {
            PacketPlayOutServices packetPlayOutServices = (PacketPlayOutServices)packet;
            this.cloudAPI.getNetwork().setServices(packetPlayOutServices.getServices());
            for (List<Service> value : packetPlayOutServices.getServices().values()) {
                for (Service service : value) {
                    if (service.getServiceGroup().getServiceType().equals(ServiceType.PROXY)) {
                        this.cloudAPI.getNetwork().getProxies().put(service.getPort(), service.getName());
                    }
                }
            }
        }
    }
}
