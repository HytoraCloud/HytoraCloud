package de.lystx.hytoracloud.driver.cloudservices.managing.event.service;

import de.lystx.hytoracloud.driver.cloudservices.managing.event.base.IEvent;
import de.lystx.hytoracloud.driver.cloudservices.managing.event.handling.IListener;
import de.lystx.hytoracloud.driver.cloudservices.managing.event.handling.IEventHandler;

public interface IEventManager {

    /**
     * Registers a class to wait for events
     *
     * @param listener the listener
     */
    void registerListener(IListener listener);

    /**
     * Unregisters a class
     *
     * @param listener the listener
     */
    void unregisterListener(IListener listener);

    /**
     * Registers an {@link IEventHandler}
     *
     * @param handler the handler
     * @param eventClass the event class
     */
    <E extends IEvent> void registerHandler(Class<E> eventClass, IEventHandler<E> handler);

    /**
     * Unregisters an {@link IEventHandler}
     *
     * @param handler the handler
     * @param eventClass the event class
     */
    <E extends IEvent> void unregisterHandler(Class<E> eventClass, IEventHandler<E> handler);

    /**
     * Calls an event for all registered classes
     *
     * @param iEvent the event to all
     * @return if event was cancelled
     */
    boolean callEvent(IEvent iEvent);
}
