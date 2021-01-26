package de.lystx.cloudsystem.library.service.player.impl;

import lombok.Getter;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.UUID;

@Getter
public class CloudConnection implements Serializable {

    private final UUID uuid;
    private final String name;
    private final InetAddress address;

    public CloudConnection(UUID uuid, String name, InetAddress address) {
        this.uuid = uuid;
        this.name = name;
        this.address = address;
    }
}
