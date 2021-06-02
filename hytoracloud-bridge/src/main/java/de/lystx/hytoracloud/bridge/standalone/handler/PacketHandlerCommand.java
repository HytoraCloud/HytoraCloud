package de.lystx.hytoracloud.bridge.standalone.handler;

import de.lystx.hytoracloud.bridge.bukkit.CloudServer;
import de.lystx.hytoracloud.bridge.proxy.CloudProxy;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.elements.packets.out.PacketCommand;
import de.lystx.hytoracloud.driver.elements.service.ServiceType;
import io.thunder.packet.Packet;
import io.thunder.packet.handler.PacketHandler;

public class PacketHandlerCommand implements PacketHandler {



    @Override
    public void handle(Packet packet) {
        if (packet instanceof PacketCommand) {
            PacketCommand packetCommand = (PacketCommand)packet;
            if (CloudDriver.getInstance().getThisService().getServiceGroup().getServiceType().equals(ServiceType.PROXY)) {
                CloudProxy.getInstance().executeCommand(packetCommand.getCommand());
                return;
            }
            if (packetCommand.getService().equalsIgnoreCase(CloudDriver.getInstance().getThisService().getName())) {
                CloudServer.getInstance().executeCommand(packetCommand.getCommand());
            }
        }
    }
}
