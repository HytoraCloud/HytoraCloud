package de.lystx.cloudsystem.library.service.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Is used for forEach loops
 * to access values
 * @param <T>
 */

@AllArgsConstructor @Getter @Setter
public class Value<T> {

    private final T value;

    public Value() {
        this(null);
    }

}
