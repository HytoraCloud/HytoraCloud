package de.lystx.cloudapi.proxy.events.other;

import de.lystx.cloudsystem.library.service.config.impl.proxy.Motd;
import de.lystx.cloudsystem.library.service.player.impl.CloudConnection;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.plugin.Cancellable;
import net.md_5.bungee.api.plugin.Event;

@Getter @RequiredArgsConstructor
public class ProxyServerMotdRequestEvent extends Event implements Cancellable {

    private final CloudConnection connection;
    private boolean cancelled;
    private Motd motd;

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    public void setMotd(Motd motd) {
        this.motd = motd;
        this.setCancelled(true);
    }

    @Override
    public void setCancelled(boolean b) {
        this.cancelled = b;
    }
}
