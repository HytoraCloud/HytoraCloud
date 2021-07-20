package de.lystx.hytoracloud.driver.commons.interfaces;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.concurrent.TimeUnit;

/**
 * This annotation shows that the method or class
 * is not filled yet but will be for the given version
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface ScheduledForVersion {

    //TODO: CHECK THIS ONES

    String value();
}
