package de.lystx.hytoracloud.bridge.proxy.events.network;

import de.lystx.hytoracloud.driver.service.player.impl.PlayerConnection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.md_5.bungee.api.plugin.Event;

@Getter @AllArgsConstructor
public class ProxyServerNetworkPingEvent extends Event {

    private final PlayerConnection playerConnection;

}
