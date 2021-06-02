package de.lystx.hytoracloud.bridge.proxy.events.other;

import lombok.Getter;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Event;

@Getter
public class ProxyServerHubCommandEvent extends Event {

    private final ProxiedPlayer player;
    private final Result result;

    public ProxyServerHubCommandEvent(ProxiedPlayer player, Result result) {
        this.player = player;
        this.result = result;
    }


    public enum Result {
        SUCCESS, ALREADY_ON_LOBBY
    }
}
