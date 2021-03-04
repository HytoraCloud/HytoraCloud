package de.lystx.cloudsystem.library.service.config.impl.proxy;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter @AllArgsConstructor
public class TabList implements Serializable {

    private final boolean enabled;
    private final String header;
    private final String footer;

}
