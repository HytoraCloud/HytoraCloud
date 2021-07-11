package de.lystx.hytoracloud.driver.commons.interfaces;

import de.lystx.hytoracloud.driver.utils.list.Filter;

/**
 * Used for {@link Filter} to check
 * if a certain action is accepted by the "Search-Request"
 *
 * @param <T> GenericType to check if its accepted
 */
public interface Acceptable<T> {

    boolean isAccepted(T t);

}
