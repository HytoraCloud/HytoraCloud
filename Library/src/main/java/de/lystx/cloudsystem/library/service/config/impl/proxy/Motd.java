package de.lystx.cloudsystem.library.service.config.impl.proxy;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter @AllArgsConstructor
public class Motd implements Serializable {

    private final boolean enabled;
    private final String firstLine;
    private final String secondLine;
    private final String protocolString;
    private final String versionString;

}
