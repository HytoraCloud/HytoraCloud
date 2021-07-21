
package de.lystx.hytoracloud.driver.commons.interfaces;


import de.lystx.hytoracloud.driver.cloudservices.global.main.CloudServiceType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CloudInfo {

    /**
     * The current version of the cloud
     *
     * @return version
     */
    String version();

    /**
     * All contributors that did something
     * for the cloud
     *
     * @return array
     */
    String[] contributors();

    /**
     * Stuff thats still to do
     *
     * @return list array
     */
    String[] todo();
}
