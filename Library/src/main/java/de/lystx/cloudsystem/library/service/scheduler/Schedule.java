package de.lystx.cloudsystem.library.service.scheduler;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Experimental Annotation to
 * Schedule Methods without having to use
 * {@link Scheduler#delayTask(Runnable, long, boolean)} or something
 * like that
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Schedule {

    long delay();

    long period() default -1L;

    boolean sync() default true;


}
