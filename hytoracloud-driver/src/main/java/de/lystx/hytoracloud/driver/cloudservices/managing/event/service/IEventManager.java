package de.lystx.hytoracloud.driver.cloudservices.managing.event.service;

import de.lystx.hytoracloud.driver.cloudservices.managing.event.base.CloudEvent;
import de.lystx.hytoracloud.driver.cloudservices.managing.event.handler.EventListener;

public interface IEventManager {

    /**
     * Registers a class to wait for events
     *
     * @param listener the listener
     */
    void registerListener(EventListener listener);

    /**
     * Unregisters a class
     *
     * @param listener the listener
     */
    void unregisterListener(EventListener listener);

    /**
     * Calls an event for all registered classes
     *
     * @param cloudEvent the event to all
     * @return if event was cancelled
     */
    boolean callEvent(CloudEvent cloudEvent);
}
