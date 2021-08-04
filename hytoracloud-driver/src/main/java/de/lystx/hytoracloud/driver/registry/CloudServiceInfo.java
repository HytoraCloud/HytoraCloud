
package de.lystx.hytoracloud.driver.registry;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CloudServiceInfo {

    /**
     * The name of the service
     */
    String name();

    /**
     * The description of the service
     */
    String[] description();

    /**
     * The version of the service
     */
    double version();

}
