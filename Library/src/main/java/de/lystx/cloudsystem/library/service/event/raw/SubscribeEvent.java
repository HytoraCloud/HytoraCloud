
package de.lystx.cloudsystem.library.service.event.raw;



import de.lystx.cloudsystem.library.service.event.enums.Priority;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface SubscribeEvent {

    Priority priority() default Priority.NORMAL;

}
