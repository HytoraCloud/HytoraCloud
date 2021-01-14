package de.lystx.cloudsystem.library.service.network.connection.channel.base;

import lombok.Getter;

@Getter
public class Provider {

    private final String name;

    public Provider(String name) {
        this.name = name;
    }

}
