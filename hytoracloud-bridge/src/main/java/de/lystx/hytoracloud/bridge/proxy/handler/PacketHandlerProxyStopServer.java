package de.lystx.hytoracloud.bridge.proxy.handler;

import de.lystx.hytoracloud.driver.elements.packets.out.PacketOutStopServer;
import io.thunder.packet.Packet;
import io.thunder.packet.handler.PacketHandler;

import net.md_5.bungee.api.ProxyServer;

public class PacketHandlerProxyStopServer implements PacketHandler {

    @Override
    public void handle(Packet packet) {
        if (packet instanceof PacketOutStopServer) {
            PacketOutStopServer packetOutStopServer = (PacketOutStopServer)packet;
            ProxyServer.getInstance().getServers().remove(packetOutStopServer.getService());
        }
    }
}
