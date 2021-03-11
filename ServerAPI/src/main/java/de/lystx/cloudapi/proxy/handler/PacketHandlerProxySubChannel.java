package de.lystx.cloudapi.proxy.handler;

import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudapi.proxy.events.network.ProxyServerSubChannelMessageEvent;
import de.lystx.cloudsystem.library.elements.packets.both.PacketSubMessage;
import de.lystx.cloudsystem.library.elements.service.ServiceType;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.md_5.bungee.api.ProxyServer;
import de.lystx.cloudsystem.library.service.network.connection.adapter.PacketHandlerAdapter;

@Getter @AllArgsConstructor
public class PacketHandlerProxySubChannel extends PacketHandlerAdapter {

    private final CloudAPI cloudAPI;


    @Override
    public void handle(Packet packet) {
        if (packet instanceof PacketSubMessage) {
            PacketSubMessage subMessage = (PacketSubMessage)packet;
            if (!subMessage.getType().equals(ServiceType.PROXY)) {
                return;
            }
            ProxyServer.getInstance().getPluginManager().callEvent(new ProxyServerSubChannelMessageEvent(subMessage.getChannel(), subMessage.getChannel(), subMessage.getDocument()));
        }
    }
}
