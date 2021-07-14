package de.lystx.hytoracloud.driver.cloudservices.global.config.impl.proxy;



import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter @AllArgsConstructor
public class Motd implements Serializable {

    /**
     * If the motd is enabled
     */
    private final boolean enabled;

    /**
     * The first line
     */
    private final String firstLine;


    /**
     * The second line
     */
    private final String secondLine;

    /**
     * The protocol line
     */
    private final String protocolString;


    /**
     * The version line
     */
    private final String versionString;

}
