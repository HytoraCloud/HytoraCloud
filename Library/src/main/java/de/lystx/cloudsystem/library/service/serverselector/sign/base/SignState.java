package de.lystx.cloudsystem.library.service.serverselector.sign.base;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@AllArgsConstructor @Getter
public enum SignState implements Serializable {


    ONLINE(1),
    OFFLINE(2),
    LOADING(3),
    LOBBY(4),
    INGAME(5),
    MAINTENANCE(6),
    UNKNOWN(7);

    private final Integer id;
}
