package de.lystx.hytoracloud.driver.utils.interfaces;


/**
 * Used to check
 * if a certain action is accepted by the "Search-Request"
 *
 * @param <T> GenericType to check if its accepted
 */
public interface Requestable<T> {

    boolean isRequested(T t);

}
