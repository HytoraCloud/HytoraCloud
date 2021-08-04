
package de.lystx.hytoracloud.driver.event.handle;



import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation declares that
 * the method it's on will handle an
 * Event.
 * You can set a custom {@link EventPriority} to
 * declare what method will be called first
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface EventHandler {

    /**
     * The priority of this handler
     *
     * @return priority
     */
    EventPriority value() default EventPriority.NORMAL;

}
