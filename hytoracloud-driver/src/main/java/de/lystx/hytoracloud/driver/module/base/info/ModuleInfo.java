
package de.lystx.hytoracloud.driver.module.base.info;


import de.lystx.hytoracloud.driver.module.cloud.DriverModule;
import de.lystx.hytoracloud.driver.utils.enums.cloud.ServerEnvironment;
import de.lystx.hytoracloud.driver.utils.enums.other.ModuleCopyType;

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

    ServerEnvironment[] allowedTypes() default ServerEnvironment.CLOUD;
}
