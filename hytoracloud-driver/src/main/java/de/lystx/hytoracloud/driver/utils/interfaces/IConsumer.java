package de.lystx.hytoracloud.driver.utils.interfaces;

/**
 * A consumer which returns a given value
 *
 * @param <R> the return-object-generic
 * @param <T> the parameter-object-generic
 */
public interface IConsumer<R, T> {

    /**
     * Consumes this consumer with a given object
     *
     * @param t the object
     * @return the r-type object
     */
    R consume(T t);
}
