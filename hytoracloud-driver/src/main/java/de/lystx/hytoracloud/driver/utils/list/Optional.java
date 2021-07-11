package de.lystx.hytoracloud.driver.utils.list;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Optional<T> {

    private final Promise<T> promise;
    private final T t;
    private final T orElse;



    public T get() {
        return this.orElse == null ? this.t : this.orElse;
    }

    public Optional<T> orElse(T t) {
        if (this.t == null) {
            return new Optional<>(this.promise, null, t);
        }
        return new Optional<>(this.promise, this.t, null);
    }
}
