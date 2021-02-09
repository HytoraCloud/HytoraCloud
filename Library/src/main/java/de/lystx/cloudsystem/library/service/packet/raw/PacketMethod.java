
package de.lystx.cloudsystem.library.service.packet.raw;

import lombok.Getter;

import java.lang.reflect.Method;

@Getter
public class PacketMethod {

    private final Object instance;
    private final Method method;
    private final Class<?> event;
    private final PacketHandler annotation;

    public PacketMethod(Object instance, Method method, Class<?> event, PacketHandler annotation) {
        this.instance = instance;
        this.method = method;
        this.event = event;
        this.annotation = annotation;
    }

}
