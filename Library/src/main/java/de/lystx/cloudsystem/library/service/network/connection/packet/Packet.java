package de.lystx.cloudsystem.library.service.network.connection.packet;

import lombok.Getter;

import java.io.Serializable;

@Getter
public class Packet implements Serializable {

    private final String key;
    private final Object object;

    public Packet(Class<?> clazz) {
        this(clazz.getSimpleName(), "no_object");
    }

    public Packet(String key, Object o) {
        this.key = key;
        this.object = o;
    }
}
