
package de.lystx.cloudsystem.library.service.event.raw;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.lang.reflect.Method;

@Getter @AllArgsConstructor
public class EventMethod {

    private final Object instance;
    private final Method method;
    private final Class<?> event;
    private final SubscribeEvent annotation;

}
