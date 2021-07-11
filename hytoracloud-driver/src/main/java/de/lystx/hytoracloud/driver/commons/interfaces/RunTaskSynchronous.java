package de.lystx.hytoracloud.driver.commons.interfaces;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.concurrent.TimeUnit;

/**
 * This annotation shows that the method or class
 * should be executed asynchronous
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface RunTaskSynchronous {

    boolean value() default true;

    long delay() default -1;

    TimeUnit unit() default TimeUnit.NANOSECONDS;

}
