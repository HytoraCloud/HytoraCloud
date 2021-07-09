package de.lystx.hytoracloud.bridge.standalone.handler;

import de.lystx.hytoracloud.bridge.bukkit.HytoraCloudBukkitBridge;
import de.lystx.hytoracloud.bridge.bungeecord.HytoraCloudBungeeCordBridge;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.elements.packets.both.PacketCommand;
import de.lystx.hytoracloud.driver.elements.service.ServiceType;
import net.hytora.networking.elements.packet.HytoraPacket;
import net.hytora.networking.elements.packet.handler.PacketHandler;
import net.hytora.networking.elements.packet.response.ResponseStatus;


public class PacketHandlerCommand implements PacketHandler {



    @Override
    public void handle(HytoraPacket packet) {
        if (packet instanceof PacketCommand) {
            PacketCommand packetCommand = (PacketCommand)packet;
            if (CloudDriver.getInstance().getThisService().getServiceGroup().getServiceType().equals(ServiceType.PROXY)) {
                HytoraCloudBungeeCordBridge.getInstance().executeCommand(packetCommand.getCommand());
                return;
            }
            if (packetCommand.getService().equalsIgnoreCase(CloudDriver.getInstance().getThisService().getName())) {
                HytoraCloudBukkitBridge.getInstance().executeCommand(packetCommand.getCommand());
            }
        }
    }
}
