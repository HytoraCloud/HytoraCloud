
package de.lystx.hytoracloud.driver.cloudservices.cloud.module.base;


import de.lystx.hytoracloud.driver.cloudservices.cloud.module.cloud.DriverModule;
import de.lystx.hytoracloud.driver.commons.enums.cloud.ServiceType;
import de.lystx.hytoracloud.driver.commons.enums.other.ModuleCopyType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ModuleTask {

    /**
     * The order id for sorting
     *
     * @return id
     */
    int id() default -1;

    /**
     * The state when it should be executed
     *
     * @return state for execution
     */
    ModuleState state();

}
