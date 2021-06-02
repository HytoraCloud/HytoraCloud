package de.lystx.hytoracloud.driver.service.event;

public interface IEventService {

    /**
     * Registers a class to wait for events
     *
     * @param classObject the object to register
     */
    void registerEvent(Object classObject);

    /**
     * Unregisters a class
     *
     * @param classObject the object to unregister
     */
    void unregister(Object classObject);

    /**
     * Calls an event for all registered classes
     *
     * @param cloudEvent the event to all
     * @return if event was cancelled
     */
    boolean callEvent(CloudEvent cloudEvent);
}
