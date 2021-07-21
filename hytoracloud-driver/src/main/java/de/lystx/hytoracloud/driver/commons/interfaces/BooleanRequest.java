package de.lystx.hytoracloud.driver.commons.interfaces;

/**
 * This class is used for example for
 * {@link de.lystx.hytoracloud.driver.CloudDriver#executeIf(Runnable, BooleanRequest)}
 * and if the request is allowed to return then the runnable gets executed
 * this is useful to prevent {@link NullPointerException} and execute the given runnable
 * first if the value is not null
 */
public interface BooleanRequest {

    /**
     * Checks if the given request is allowed
     *
     * @return boolean
     */
    boolean isAccepted();

}
