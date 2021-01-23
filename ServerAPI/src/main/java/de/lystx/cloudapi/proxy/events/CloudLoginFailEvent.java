package de.lystx.cloudapi.proxy.events;

import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.plugin.Cancellable;
import net.md_5.bungee.api.plugin.Event;

@Getter @Setter
public class CloudLoginFailEvent extends Event implements Cancellable {

    private final PendingConnection connection;
    private final Reason reason;
    private String cancelReason;
    private boolean cancelled;

    public CloudLoginFailEvent(PendingConnection connection, Reason reason) {
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
        NO_SERVICES,
        ALREADY_ON_NETWORK,
        MAINTENANCE
    }
}