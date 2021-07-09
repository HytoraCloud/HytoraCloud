package de.lystx.hytoracloud.driver.service.config.impl.proxy;



import io.vson.elements.object.Objectable;
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
