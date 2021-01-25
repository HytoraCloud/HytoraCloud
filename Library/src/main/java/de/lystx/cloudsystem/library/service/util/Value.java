package de.lystx.cloudsystem.library.service.util;


public class Value<T> {

    private T value;

    public Value(T value) {
        this.value = value;
    }

    public Value() {
        this(null);
    }

    public T get() {
        return value;
    }

    public void set(T value) {
        this.value = value;
    }
}
