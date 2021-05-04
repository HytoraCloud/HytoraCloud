package de.lystx.cloudsystem.library.network.packet;


import de.lystx.cloudsystem.library.enums.Priority;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for methods inside class<br>
 * These methods needs following erasure: public void $name($packet_class) and with this annotation
 */
@Retention(value = RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface PacketListener {

    /**
     * Priority of the event
     * @return The event priority object
     * @see Priority
     */
    Priority value() default Priority.NORMAL;

}
