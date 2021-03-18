
package de.lystx.cloudsystem.library.service.network.packet;



import de.lystx.cloudsystem.library.enums.Priority;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface PacketHandler {

    Priority value() default Priority.NORMAL; //Priority of this handler

    Class<? extends Packet> transformTo() default Packet.class; //Used for custom packets

}
