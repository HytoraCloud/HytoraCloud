
package de.lystx.hytoracloud.networking.provided.objects;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.lang.reflect.Method;

/**
 * Used to store Annotations for
 * @param <T>
 */
@Getter @AllArgsConstructor
public class PacketMethod<T> {

    private final Object instance;
    private final Method method;
    private final Class<?> aClass;
    private final T annotation;

}
