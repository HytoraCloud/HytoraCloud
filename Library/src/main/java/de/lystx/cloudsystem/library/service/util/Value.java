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
     * If the value is a {@link Integer}
     * it will increase by 1
     */
    public void increase() {
        if (value instanceof Integer) {
            Integer integer = (Integer) this.value;
            integer++;
            this.value = (T) integer;
        } else {
            throw new UnsupportedOperationException("Can't increase value of type " + this.value.getClass().getSimpleName());
        }
    }

    /**
     * If the value is a {@link Boolean}
     * you can change the boolean
     *
     * for example if it's currently false it will
     * will be changed to true but if its true
     * it will be changed to false
     */
    public void change() {
        if (value instanceof Boolean) {
            Boolean aBoolean = (Boolean) this.value;
            aBoolean = !aBoolean;
            this.value = (T) aBoolean;
        } else {
            throw new UnsupportedOperationException("Can't increase value of type " + this.value.getClass().getSimpleName());
        }
    }

    /**
     * If the value is a {@link Integer}
     * it will decrease by 1
     */
    public void decrease() {
        if (value instanceof Integer) {
            Integer integer = (Integer) this.value;
            integer--;
            this.value = (T) integer;
        } else {
            throw new UnsupportedOperationException("Can't decrease value of type " + this.value.getClass().getSimpleName());
        }
    }

    /**
     * Returns value
     * @return
     */
    public T getValue() {
        return value;
    }
}
