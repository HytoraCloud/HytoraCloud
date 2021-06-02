package de.lystx.hytoracloud.bridge.proxy.events.network;

import io.thunder.packet.Packet;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.md_5.bungee.api.plugin.Event;

@Getter @AllArgsConstructor
public class ProxyServerPacketReceiveEvent extends Event {

    private final Packet packet;

}
