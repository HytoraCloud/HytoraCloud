package de.lystx.cloudsystem.library.service.config.impl.proxy;

import lombok.Getter;

import java.io.Serializable;

@Getter
public class TabList implements Serializable {

    private final boolean enabled;
    private final String header;
    private final String footer;

    public TabList(boolean enabled, String header, String footer) {
        this.enabled = enabled;
        this.header = header;
        this.footer = footer;
    }
}
