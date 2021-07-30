package de.lystx.hytoracloud.networking.elements.packet.response;

import de.lystx.hytoracloud.networking.elements.component.Component;

public interface Response<V> {

    /**
     * Gets the object from this response
     *
     * @return the response
     */
    V get();

    /**
     * Gets the raw Component
     *
     * @return component
     */
    Component getComponent();

    /**
     * Gets the status of this response
     *
     * @return the status
     */
    ResponseStatus getStatus();
}
