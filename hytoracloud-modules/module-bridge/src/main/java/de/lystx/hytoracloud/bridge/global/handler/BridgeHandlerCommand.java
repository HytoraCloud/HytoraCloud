package de.lystx.hytoracloud.bridge.global.handler;

import de.lystx.hytoracloud.bridge.CloudBridge;
import de.lystx.hytoracloud.driver.commons.packets.both.PacketCommand;
import de.lystx.hytoracloud.networking.elements.packet.Packet;
import de.lystx.hytoracloud.networking.elements.packet.handler.PacketHandler;


public class BridgeHandlerCommand implements PacketHandler {



    @Override
    public void handle(Packet packet) {
        if (packet instanceof PacketCommand) {
            PacketCommand packetCommand = (PacketCommand)packet;
            CloudBridge.getInstance().getBridgeInstance().flushCommand(packetCommand.getCommand());
        }
    }
}
