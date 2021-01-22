package de.lystx.cloudapi.proxy.events;

import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import lombok.Getter;
import net.md_5.bungee.api.plugin.Event;

@Getter
public class ProxyPacketReceiveEvent extends Event {

    private final Packet packet;

    public ProxyPacketReceiveEvent(Packet packet) {
        this.packet = packet;
    }
}
