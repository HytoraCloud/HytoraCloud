package de.lystx.cloudsystem.library.value;

import lombok.Getter;

import java.io.Serializable;

@Getter
public class Value<F, S> implements Serializable {

    private final F key;
    private final S value;

    public Value(F key, S value) {
        this.key = key;
        this.value = value;
    }


}
