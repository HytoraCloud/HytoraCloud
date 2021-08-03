package de.lystx.hytoracloud.driver.event.handle;

import de.lystx.hytoracloud.driver.event.IEvent;

/**
 * This is a simple event handler
 * which can only handle one event
 *
 * @param <E> the generic event
 */
public interface IEventHandler<E extends IEvent> {

    /**
     * Called when the given event is handled
     *
     * @param event the event
     */
    void handle(E event);

}
