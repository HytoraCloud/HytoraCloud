package de.lystx.cloudapi.proxy.handler;

import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudapi.proxy.events.ProxyServerSubChannelMessageEvent;
import de.lystx.cloudsystem.library.elements.other.NetworkHandler;
import de.lystx.cloudsystem.library.elements.packets.communication.PacketCommunicationSubMessage;
import de.lystx.cloudsystem.library.service.network.connection.adapter.PacketHandlerAdapter;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import lombok.Getter;
import net.md_5.bungee.api.ProxyServer;

@Getter
public class PacketHandlerProxySubChannel extends PacketHandlerAdapter {

    private final CloudAPI cloudAPI;

    public PacketHandlerProxySubChannel(CloudAPI cloudAPI) {
        this.cloudAPI = cloudAPI;
    }

    @Override
    public void handle(Packet packet) {
        if (packet instanceof PacketCommunicationSubMessage) {
            PacketCommunicationSubMessage subMessage = (PacketCommunicationSubMessage)packet;
            ProxyServer.getInstance().getPluginManager().callEvent(new ProxyServerSubChannelMessageEvent(subMessage.getChannel(), subMessage.getChannel(), subMessage.getDocument()));
        }
    }
}
