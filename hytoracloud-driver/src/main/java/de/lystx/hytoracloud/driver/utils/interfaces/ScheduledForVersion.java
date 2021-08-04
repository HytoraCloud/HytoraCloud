package de.lystx.hytoracloud.driver.utils.interfaces;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * This annotation shows that the method or class
 * is not filled yet but will be for the given version
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface ScheduledForVersion {

    String value();
}
