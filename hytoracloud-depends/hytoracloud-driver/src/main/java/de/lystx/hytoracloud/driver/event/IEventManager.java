package de.lystx.hytoracloud.driver.event;

import de.lystx.hytoracloud.driver.event.handle.IEventHandler;
import de.lystx.hytoracloud.driver.event.handle.IListener;

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
     * Calls an Event with the driver
     * If this instance is bridge it calls an event
     * and sets this service on blacklist to receive the same event again
     * to prevent double-executing events
     *
     * If this instance is cloud it just sends packets to all
     * clients and sets the cloud on blacklist to receive the same event again
     *
     * @param event the event to call
     */
    boolean callEvent(IEvent event);
}
