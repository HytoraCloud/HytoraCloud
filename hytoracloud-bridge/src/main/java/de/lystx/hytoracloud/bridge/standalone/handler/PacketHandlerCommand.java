package de.lystx.hytoracloud.bridge.standalone.handler;

import de.lystx.hytoracloud.bridge.bukkit.BukkitBridge;
import de.lystx.hytoracloud.bridge.bungeecord.BungeeBridge;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.commons.packets.both.PacketCommand;
import de.lystx.hytoracloud.driver.commons.service.ServiceType;
import net.hytora.networking.elements.packet.HytoraPacket;
import net.hytora.networking.elements.packet.handler.PacketHandler;


public class PacketHandlerCommand implements PacketHandler {



    @Override
    public void handle(HytoraPacket packet) {
        if (packet instanceof PacketCommand) {
            PacketCommand packetCommand = (PacketCommand)packet;
            if (CloudDriver.getInstance().getThisService().getServiceGroup().getServiceType().equals(ServiceType.PROXY)) {
                BungeeBridge.getInstance().executeCommand(packetCommand.getCommand());
                return;
            }
            if (packetCommand.getService().equalsIgnoreCase(CloudDriver.getInstance().getThisService().getName())) {
                BukkitBridge.getInstance().executeCommand(packetCommand.getCommand());
            }
        }
    }
}
