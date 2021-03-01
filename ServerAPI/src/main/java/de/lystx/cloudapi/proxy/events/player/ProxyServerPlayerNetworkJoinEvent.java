package de.lystx.cloudapi.proxy.events.player;

import de.lystx.cloudsystem.library.service.player.impl.CloudPlayer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.md_5.bungee.api.plugin.Event;

@Getter @AllArgsConstructor
public class ProxyServerPlayerNetworkJoinEvent extends Event {

    private final CloudPlayer cloudPlayer;

}
