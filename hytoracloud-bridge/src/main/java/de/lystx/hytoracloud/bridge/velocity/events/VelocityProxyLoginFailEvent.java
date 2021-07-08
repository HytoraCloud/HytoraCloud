package de.lystx.hytoracloud.bridge.velocity.events;

import de.lystx.hytoracloud.driver.service.player.impl.PlayerConnection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter @Setter
public class VelocityProxyLoginFailEvent {

    private final PlayerConnection connection;
    private final VelocityProxyLoginFailEvent.Reason reason;
    private String cancelReason;
    private boolean cancelled;

    public void setCancelReason(String cancelReason) {
        this.cancelled = true;
        this.cancelReason = cancelReason;
    }

    public enum Reason {
        ALREADY_ON_NETWORK,
        NETWORK_FULL,
        MAINTENANCE
    }
}
