
package de.lystx.hytoracloud.driver.service.managing.event.handler;



import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation declares that
 * the method it's on will handle an
 * Event.
 * You can set a custom {@link CloudPriority} to
 * declare what method will be called first
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Event {

    /**
     * The priority of this handler
     *
     * @return priority
     */
    CloudPriority value() default CloudPriority.NORMAL;

}
