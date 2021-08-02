
package de.lystx.hytoracloud.driver.cloudservices.cloud.module.base;


import de.lystx.hytoracloud.driver.cloudservices.cloud.module.cloud.DriverModule;
import de.lystx.hytoracloud.driver.commons.enums.cloud.ServiceType;
import de.lystx.hytoracloud.driver.commons.enums.other.ModuleCopyType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ModuleInfo {

    String name();

    String[] authors();

    String description();

    String version();

    String website();

    Class<? extends DriverModule> main();

    ModuleCopyType copyType();

    ServiceType[] allowedTypes() default ServiceType.CLOUDSYSTEM;
}
