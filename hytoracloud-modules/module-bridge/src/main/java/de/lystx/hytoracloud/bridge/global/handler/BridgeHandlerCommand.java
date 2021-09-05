package de.lystx.hytoracloud.bridge.global.handler;

import de.lystx.hytoracloud.bridge.CloudBridge;
import de.lystx.hytoracloud.driver.connection.protocol.netty.global.packet.IPacket;
import de.lystx.hytoracloud.driver.connection.protocol.netty.global.packet.handling.IPacketHandler;
import de.lystx.hytoracloud.driver.packets.both.PacketCommand;

public class BridgeHandlerCommand implements IPacketHandler {



    @Override
    public void handle(IPacket packet) {
        if (packet instanceof PacketCommand) {
            PacketCommand packetCommand = (PacketCommand)packet;
            CloudBridge.getInstance().getBridgeInstance().flushCommand(packetCommand.getCommand());
        }
    }
}
