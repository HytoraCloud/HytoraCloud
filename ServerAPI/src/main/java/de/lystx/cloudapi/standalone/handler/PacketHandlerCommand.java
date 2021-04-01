package de.lystx.cloudapi.standalone.handler;

import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudapi.bukkit.CloudServer;
import de.lystx.cloudapi.proxy.CloudProxy;
import de.lystx.cloudsystem.library.elements.packets.out.service.PacketCommand;
import de.lystx.cloudsystem.library.elements.service.ServiceType;
import de.lystx.cloudsystem.library.service.network.packet.PacketHandler;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor @Getter
public class PacketHandlerCommand  {

    private final CloudAPI cloudAPI;


    @PacketHandler
    public void handlePacket(PacketCommand packet) {

        if (cloudAPI.getService().getServiceGroup().getServiceType().equals(ServiceType.PROXY)) {
            CloudProxy.getInstance().executeCommand(packet.getString("command"));
            return;
        }
        if (packet.getString("service").equalsIgnoreCase(cloudAPI.getService().getName())) {
            CloudServer.getInstance().executeCommand(packet.getString("command"));
        }
    }
}
