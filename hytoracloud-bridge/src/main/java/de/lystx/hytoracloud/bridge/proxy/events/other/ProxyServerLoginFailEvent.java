package de.lystx.hytoracloud.bridge.proxy.events.other;

import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.plugin.Cancellable;
import net.md_5.bungee.api.plugin.Event;

@Getter @Setter
public class ProxyServerLoginFailEvent extends Event implements Cancellable {

    private final PendingConnection connection;
    private final Reason reason;
    private String cancelReason;
    private boolean cancelled;

    public ProxyServerLoginFailEvent(PendingConnection connection, Reason reason) {
        this.connection = connection;
        this.reason = reason;
        this.cancelled = false;
    }

    public void setCancelReason(String cancelReason) {
        this.setCancelled(true);
        this.cancelReason = cancelReason;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        this.cancelled = b;
    }


    public enum Reason {
        ALREADY_ON_NETWORK,
        NETWORK_FULL,
        MAINTENANCE
    }
}
