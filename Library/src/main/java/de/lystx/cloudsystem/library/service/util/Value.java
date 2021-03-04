package de.lystx.cloudsystem.library.service.util;

import lombok.AllArgsConstructor;

/**
 * Is used for forEach loops
 * to access values
 * @param <T>
 */

@AllArgsConstructor
public class Value<T> {

    private T value;

    public Value() {
        this(null);
    }

    /**
     * Sets value
     * @param value
     */
    public void setValue(T value) {
        this.value = value;
    }

    /**
     * Returns value
     * @return
     */
    public T getValue() {
        return value;
    }
}
