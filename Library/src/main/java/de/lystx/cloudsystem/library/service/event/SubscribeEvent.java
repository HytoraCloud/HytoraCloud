
package de.lystx.cloudsystem.library.service.event;



import de.lystx.cloudsystem.library.enums.Priority;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface SubscribeEvent {

    Priority value() default Priority.NORMAL;

}
