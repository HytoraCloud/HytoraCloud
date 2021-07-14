package de.lystx.hytoracloud.driver.cloudservices.global.config.impl.proxy;



import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter @AllArgsConstructor
public class TabList implements Serializable {

    /**
     * If the tablist is enabled
     */
    private final boolean enabled;

    /**
     * The header
     */
    private final String header;

    /**
     * The footer
     */
    private final String footer;

}
