package de.lystx.hytoracloud.networking.packet.manager;


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

}
