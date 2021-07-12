
package de.lystx.hytoracloud.driver.cloudservices.managing.event.base;

import de.lystx.hytoracloud.driver.cloudservices.managing.event.handler.Event;

import de.lystx.hytoracloud.driver.cloudservices.managing.event.handler.EventListener;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.lang.reflect.Method;

/**
 * Used to store Annotations for
 *  and {@link Event}
 * @param <T>
 */
@Getter @AllArgsConstructor
public class EventMethod<T> {

    private final EventListener listener;
    private final Method method;
    private final Class<?> aClass;
    private final T annotation;

}
