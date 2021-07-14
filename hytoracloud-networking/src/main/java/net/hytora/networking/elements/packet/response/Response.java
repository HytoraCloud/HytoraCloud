package net.hytora.networking.elements.packet.response;

import net.hytora.networking.elements.component.Component;
import net.hytora.networking.elements.packet.response.ResponseStatus;

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
