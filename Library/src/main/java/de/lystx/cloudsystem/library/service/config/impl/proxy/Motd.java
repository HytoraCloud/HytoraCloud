package de.lystx.cloudsystem.library.service.config.impl.proxy;

import lombok.Getter;

import java.io.Serializable;

@Getter
public class Motd implements Serializable {

    private final boolean enabled;
    private final String firstLine;
    private final String secondLine;
    private final String protocolString;
    private final String versionString;

    public Motd(boolean enabled, String firstLine, String secondLine, String protocolString, String versionString) {
        this.enabled = enabled;
        this.firstLine = firstLine;
        this.secondLine = secondLine;
        this.protocolString = protocolString;
        this.versionString = versionString;
    }
}
