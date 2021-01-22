package de.lystx.cloudapi.standalone.handler;

import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudapi.bukkit.CloudServer;
import de.lystx.cloudsystem.library.elements.service.ServiceType;
import de.lystx.cloudsystem.library.service.network.connection.adapter.PacketHandlerAdapter;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor @Getter
public class PacketHandlerCommand extends PacketHandlerAdapter {

    private final CloudAPI cloudAPI;

    @Override
    public void handle(Packet packet) {
        if (packet.document().isEmpty()) {
            return;
        }
        if (cloudAPI.getService().getServiceGroup().getServiceType().equals(ServiceType.PROXY)) {
            return;
        }
        if (packet.document().getString("key", "elseKey").equalsIgnoreCase("executeCommand")) {
            if (packet.document().getString("service").equalsIgnoreCase(cloudAPI.getService().getName())) {
                CloudServer.getInstance().executeCommand(packet.document().getString("command"));
            }
        }
    }
}
