
package de.lystx.cloudsystem.library.service.event.raw;

import lombok.Getter;

import java.lang.reflect.Method;

@Getter
public class EventMethod {

    private final Object instance;
    private final Method method;
    private final Class<?> event;
    private final SubscribeEvent annotation;

    public EventMethod(Object instance, Method method, Class<?> event, SubscribeEvent annotation) {
        this.instance = instance;
        this.method = method;
        this.event = event;
        this.annotation = annotation;
    }

}
