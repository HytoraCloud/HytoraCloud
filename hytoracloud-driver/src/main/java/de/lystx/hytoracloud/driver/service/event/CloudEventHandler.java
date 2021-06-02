
package de.lystx.hytoracloud.driver.service.event;



import de.lystx.hytoracloud.driver.enums.Priority;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation declares that
 * the method it's on will handle an
 * Event.
 * You can set a custom {@link Priority} to
 * declare what method will be called first
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface CloudEventHandler {

    Priority value() default Priority.NORMAL;

}
