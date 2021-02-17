package de.lystx.cloudapi.standalone.handler;

import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudapi.bukkit.CloudServer;
import de.lystx.cloudapi.proxy.CloudProxy;
import de.lystx.cloudsystem.library.elements.packets.out.service.PacketPlayOutExecuteCommand;
import de.lystx.cloudsystem.library.elements.service.ServiceType;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import de.lystx.cloudsystem.library.service.packet.raw.PacketHandler;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor @Getter
public class PacketHandlerCommand  {

    private final CloudAPI cloudAPI;


    @PacketHandler
    public void handlePacket(PacketPlayOutExecuteCommand packet) {

        if (cloudAPI.getService().getServiceGroup().getServiceType().equals(ServiceType.PROXY)) {
            CloudProxy.getInstance().executeCommand(packet.document().getString("command"));
            return;
        }
        if (packet.document().getString("service").equalsIgnoreCase(cloudAPI.getService().getName())) {
            CloudServer.getInstance().executeCommand(packet.document().getString("command"));
        }
    }
}
