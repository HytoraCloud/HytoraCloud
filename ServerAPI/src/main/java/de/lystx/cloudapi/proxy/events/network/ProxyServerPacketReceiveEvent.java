package de.lystx.cloudapi.proxy.events.network;

import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import lombok.Getter;
import net.md_5.bungee.api.plugin.Event;

@Getter
public class ProxyServerPacketReceiveEvent extends Event {

    private final Packet packet;

    public ProxyServerPacketReceiveEvent(Packet packet) {
        this.packet = packet;
    }
}
