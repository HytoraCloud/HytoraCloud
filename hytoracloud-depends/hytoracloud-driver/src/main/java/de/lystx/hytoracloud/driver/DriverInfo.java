
package de.lystx.hytoracloud.driver;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface DriverInfo {

    /**
     * The current version of the cloud
     *
     * @return version
     */
    String version();

    /**
     * The lowest cloud supported version
     *
     * @return version string
     */
    String lowestSupportVersion();

    /**
     * The allowed java versions
     * that run this cloud
     *
     * @return version array
     */
    String[] allowedJavaVersions();

    /**
     * The highest cloud supported version
     *
     * @return version string
     */
    String highestSupportVersion();

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
