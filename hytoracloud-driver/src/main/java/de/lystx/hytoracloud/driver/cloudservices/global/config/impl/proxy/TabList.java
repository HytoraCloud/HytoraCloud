package de.lystx.hytoracloud.driver.cloudservices.global.config.impl.proxy;



import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter @AllArgsConstructor
public class TabList implements Serializable {

    private boolean enabled;
    private String header;
    private String footer;

}
