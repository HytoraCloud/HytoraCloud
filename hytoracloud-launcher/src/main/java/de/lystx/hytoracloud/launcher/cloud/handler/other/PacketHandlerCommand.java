package de.lystx.hytoracloud.launcher.cloud.handler.other;

import de.lystx.hytoracloud.launcher.cloud.CloudSystem;
import de.lystx.hytoracloud.driver.commons.packets.both.PacketCommand;
import net.hytora.networking.elements.packet.HytoraPacket;
import net.hytora.networking.elements.packet.handler.PacketHandler;

import de.lystx.hytoracloud.driver.service.managing.command.CommandService;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor @Getter
public class PacketHandlerCommand implements PacketHandler {

    private final CloudSystem cloudSystem;


    @Override
    public void handle(HytoraPacket packet) {
        if (packet instanceof PacketCommand) {
            PacketCommand packetCommand = (PacketCommand)packet;
            if (packetCommand.getService().equalsIgnoreCase("null")) {
                cloudSystem.getInstance(CommandService.class).execute(packetCommand.getCommand(), cloudSystem.getParent().getConsole());
            }
        }
    }
}
