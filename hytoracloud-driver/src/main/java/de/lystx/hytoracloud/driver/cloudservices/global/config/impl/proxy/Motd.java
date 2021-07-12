package de.lystx.hytoracloud.driver.cloudservices.global.config.impl.proxy;



import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter @AllArgsConstructor
public class Motd implements Serializable {

    private boolean enabled;
    private String firstLine;
    private String secondLine;
    private String protocolString;
    private String versionString;

}
