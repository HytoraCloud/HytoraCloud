
package de.lystx.cloudsystem.library.service.network.packet.raw;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.lang.reflect.Method;

@Getter @AllArgsConstructor
public class PacketMethod {

    private final Object instance;
    private final Method method;
    private final Class<?> event;
    private final PacketHandler annotation;

}
