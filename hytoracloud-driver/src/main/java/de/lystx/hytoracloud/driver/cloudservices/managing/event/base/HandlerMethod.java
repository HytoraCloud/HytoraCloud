
package de.lystx.hytoracloud.driver.cloudservices.managing.event.base;

import de.lystx.hytoracloud.driver.cloudservices.managing.event.handler.EventMarker;

import de.lystx.hytoracloud.driver.cloudservices.managing.event.handler.EventListener;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.lang.reflect.Method;

/**
 * Used to store Annotations for
 *  and {@link EventMarker}
 * @param <T>
 */
@Getter @RequiredArgsConstructor
public class HandlerMethod<T> {

    private final Object listener;
    private final Method method;
    private final Class<?> aClass;
    private final T annotation;

    @Setter
    private Object[] objects;
}
