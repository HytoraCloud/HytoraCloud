package de.lystx.cloudsystem.library.elements.interfaces;

public interface ValueConsumer<T> {

    /**
     * Used to return the value
     * U can change value within consumer
     * @param t
     * @return
     */
    T consume(T t);
}
