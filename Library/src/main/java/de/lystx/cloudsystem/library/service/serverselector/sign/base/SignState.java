package de.lystx.cloudsystem.library.service.serverselector.sign.base;

import java.io.Serializable;

public enum SignState implements Serializable {

    ONLINE(1),
    OFFLINE(2),
    LOADING(3),
    LOBBY(4),
    INGAME(5),
    MAINTENANCE(6),
    UNKNOWN(7);

    private Integer id;

    SignState(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }
}
