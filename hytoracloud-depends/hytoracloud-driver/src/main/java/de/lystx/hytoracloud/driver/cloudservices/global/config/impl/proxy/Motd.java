package de.lystx.hytoracloud.driver.cloudservices.global.config.impl.proxy;



import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter @AllArgsConstructor
public class Motd implements Serializable {

    private static final long serialVersionUID = -4225937531956533199L;

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
     * The playerInfo lines
     */
    private final String[] playerInfo;

    /**
     * The version line
     */
    private final String versionString;

}
