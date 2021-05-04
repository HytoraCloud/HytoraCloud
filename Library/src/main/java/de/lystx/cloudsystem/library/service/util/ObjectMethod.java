
package de.lystx.cloudsystem.library.service.util;

import de.lystx.cloudsystem.library.service.event.CloudEventHandler;
import de.lystx.cloudsystem.library.service.network.packet.PacketHandler;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.lang.reflect.Method;

/**
 * Used to store Annotations for
 * {@link PacketHandler} and {@link CloudEventHandler}
 * @param <T>
 */
@Getter @AllArgsConstructor
public class ObjectMethod<T> {

    private final Object instance;
    private final Method method;
    private final Class<?> aClass;
    private final T annotation;

}
