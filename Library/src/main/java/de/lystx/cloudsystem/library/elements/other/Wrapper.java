package de.lystx.cloudsystem.library.elements.other;

import lombok.Getter;

import java.io.Serializable;

@Getter
public class Wrapper implements Serializable {

    private final String name;
    private final String ipAddress;
    private final int port;

    public Wrapper(String name, String ipAddress, int port) {
        this.name = name;
        this.ipAddress = ipAddress;
        this.port = port;
    }
}
