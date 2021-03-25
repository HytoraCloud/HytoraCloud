package de.lystx.cloudsystem.library.elements.interfaces;

/**
 * Class is used to modify the Value
 * within the Consumer and returns
 * the value
 * @param <T> Generic Type to consume
 */
public interface ValueConsumer<T> {

    /**
     * Used to return the value
     * U can change value within consumer
     * @param t
     * @return
     */
    T consume(T t);
}
