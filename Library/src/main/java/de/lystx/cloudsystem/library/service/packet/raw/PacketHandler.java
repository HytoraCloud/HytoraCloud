
package de.lystx.cloudsystem.library.service.packet.raw;



import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import de.lystx.cloudsystem.library.service.packet.enums.PacketPriority;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface PacketHandler {

    PacketPriority priority() default PacketPriority.NORMAL;

    Class<? extends Packet> transformTo() default Packet.class;

}
