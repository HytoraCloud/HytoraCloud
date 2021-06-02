
package de.lystx.hytoracloud.driver.service.main;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ICloudServiceInfo {

    String name();

    CloudServiceType type();

    String[] description();

    double version();
}
