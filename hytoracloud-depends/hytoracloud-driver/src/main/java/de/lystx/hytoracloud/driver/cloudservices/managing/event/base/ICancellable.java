package de.lystx.hytoracloud.driver.cloudservices.managing.event.base;

import java.io.Serializable;

public interface ICancellable extends Serializable {

    /**
     * Checks if the event is cancelled
     * @return boolean
     */
    boolean isCancelled();

    /**
     * Sets the state of this event
     *
     * @param cancelled if cancelled or not
     */
    void setCancelled(boolean cancelled);
}
