package de.lystx.cloudapi.proxy.events.network;

import de.lystx.cloudsystem.library.service.player.impl.CloudConnection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.md_5.bungee.api.plugin.Event;

@Getter @AllArgsConstructor
public class ProxyServerNetworkPingEvent extends Event {

    private final CloudConnection cloudConnection;

}
