package de.lystx.hytoracloud.driver.service.managing.event.service;

import de.lystx.hytoracloud.driver.service.managing.event.base.CloudEvent;
import de.lystx.hytoracloud.driver.service.managing.event.handler.EventListener;

public interface IEventService {

    /**
     * Registers a class to wait for events
     *
     * @param listener the listener
     */
    void registerEvent(EventListener listener);

    /**
     * Unregisters a class
     *
     * @param listener the listener
     */
    void unregister(EventListener listener);

    /**
     * Calls an event for all registered classes
     *
     * @param cloudEvent the event to all
     * @return if event was cancelled
     */
    boolean callEvent(CloudEvent cloudEvent);
}
